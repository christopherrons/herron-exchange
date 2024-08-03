import { useCallback, useEffect, useRef, useState } from "react";
import { stompSubcription } from "../../common/stomp-client";
import { PriceQuote, Message, StateChange, Trade, Event, TopOfBook } from "../../common/types";
import { isStateChange, isTopOfBook, isTrade } from "../../common/utils";
import SpreadChart from "./SpreadChart";
import { throttle } from "lodash";
import { fetchOrderbookState } from "../../common/rest-client";

interface Spread {
  orderbookId: string;
  state: string;
  askQuotes: PriceQuote[];
  bidQuotes: PriceQuote[];
  trades: Trade[];
}

interface Props {
  orderbookId: string;
}

const maxNrOfSeconds: number = 30;
const updateFrequencyMs: number = 1000;

const initCache = (orderbookId: string) => {
  return {
    orderbookId: orderbookId,
    state: "CLOSED",
    askQuotes: [],
    bidQuotes: [],
    trades: [],
  };
};

const updateCacheSpread = (message: Message, prevSpread: Spread): Spread => {
  if (isTopOfBook(message)) {
    const topOfBook: TopOfBook = message as TopOfBook;

    const updatedBidQuotes =
      topOfBook.bidQuote?.quoteType === "BID_PRICE"
        ? [...prevSpread.bidQuotes, { ...topOfBook.bidQuote, timeOfEvent: topOfBook.timeOfEvent }]
        : prevSpread.bidQuotes;

    const updatedAskQuotes =
      topOfBook.askQuote?.quoteType === "ASK_PRICE"
        ? [...prevSpread.askQuotes, { ...topOfBook.askQuote, timeOfEvent: topOfBook.timeOfEvent }]
        : prevSpread.askQuotes;

    return {
      ...prevSpread,
      bidQuotes: updatedBidQuotes,
      askQuotes: updatedAskQuotes,
      state: prevSpread.state,
    };
  } else if (isStateChange(message)) {
    const stateChange: StateChange = message as StateChange;

    return {
      ...prevSpread,
      state: stateChange.tradeState,
    };
  } else if (isTrade(message)) {
    const trade: Trade = message as Trade;

    return {
      ...prevSpread,
      trades: [...prevSpread.trades, trade],
    };
  }

  return prevSpread;
};

const filterAllEvents = (bidQuotes: PriceQuote[], askQuotes: PriceQuote[], trades: Trade[]) => {
  const latestTimeStamp = Math.max(
    bidQuotes[bidQuotes.length - 1]?.timeOfEvent.timeStampMs ?? 0,
    askQuotes[askQuotes.length - 1]?.timeOfEvent.timeStampMs ?? 0,
    trades[trades.length - 1]?.timeOfEvent.timeStampMs ?? 0
  );

  const filteredBidQuotes: PriceQuote[] = filterRecentEvents(bidQuotes, latestTimeStamp);

  const earlistTimestamp: number = filteredBidQuotes[0]?.timeOfEvent.timeStampMs ?? 0;
  const filterOldEvents = <T extends Event>(events: T[]) => {
    return events.filter((event: T) => event.timeOfEvent.timeStampMs >= earlistTimestamp);
  };

  return {
    bidQuotes: filteredBidQuotes,
    askQuotes: filterOldEvents(askQuotes),
    trades: filterOldEvents(trades),
  };
};

const filterRecentEvents = <T extends Event>(events: T[], latestTimeStamp: number) => {
  return events.filter((event: T) =>
    isWithinLastNSeconds(latestTimeStamp, event.timeOfEvent.timeStampMs, maxNrOfSeconds)
  );
};

function LiveMarketSpreadChart({ orderbookId }: Props) {
  const cacheSpread = useRef<Spread>(initCache(orderbookId));

  const [spread, setSpread] = useState<Spread>(cacheSpread.current);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const handleMessage = (message: Message) => {
    cacheSpread.current = updateCacheSpread(message, cacheSpread.current);
    throttledSetSpread(cacheSpread.current);
  };

  const throttledSetSpread = useCallback(
    throttle((newSpread: Spread) => {
      const { bidQuotes, askQuotes, trades } = filterAllEvents(
        newSpread.bidQuotes,
        newSpread.askQuotes,
        newSpread.trades
      );
      const filteredSpread: Spread = {
        ...newSpread,
        bidQuotes: bidQuotes,
        askQuotes: askQuotes,
        trades: trades,
      };
      setSpread(filteredSpread);
    }, updateFrequencyMs),
    []
  );

  useEffect(() => {
    cacheSpread.current = initCache(orderbookId);
    setSpread(cacheSpread.current);
    const getOrderbookState = async () => {
      try {
        const state: string = await fetchOrderbookState(orderbookId);
        cacheSpread.current = {
          ...cacheSpread.current,
          state: state,
        };
      } catch (error: any) {
        setError(error.message);
      } finally {
        setLoading(false);
        setSpread(cacheSpread.current);
      }
    };

    getOrderbookState();

    const topics = [
      "/topic/trades/" + orderbookId,
      "/topic/topOfBook/" + orderbookId,
      "/topic/stateChange/" + orderbookId,
    ];
    const subscription = stompSubcription({
      id: "Spread Chart",
      topics: topics,
      handleMessage: (message) => handleMessage(message),
    });
    return () => {
      subscription.unsubscribe();
    };
  }, [orderbookId]);

  return (
    <div>
      <SpreadChart spread={spread}></SpreadChart>
    </div>
  );
}

const isWithinLastNSeconds = (
  latestTimestamp: number,
  eventTimestamp: number,
  seconds: number
): boolean => {
  return latestTimestamp - eventTimestamp < seconds * 1000;
};

export default LiveMarketSpreadChart;
