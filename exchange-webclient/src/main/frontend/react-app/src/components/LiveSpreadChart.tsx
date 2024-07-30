import { useCallback, useEffect, useRef, useState } from "react";
import { stompSubcription } from "../common/StompClient";
import { PriceQuote, Message, StateChange, Trade, Event, TopOfBook } from "../common/Types";
import { isStateChange, isTopOfBook, isTrade } from "../common/Utils";
import SpreadChart from "./SpreadChart";
import { throttle } from "lodash";

interface Spread {
  orderbook: string;
  state: string;
  askQuotes: PriceQuote[];
  bidQuotes: PriceQuote[];
  trades: Trade[];
}

interface Props {
  orderbook: string;
}

const maxNrOfEvents: number = 1000;
const maxNrOfSeconds: number = 30;
const updateFrequencyMs: number = 1000;

const updateCacheSpread = (message: Message, prevSpread: Spread): Spread => {
  const filterAllEvents = (bidQuotes: PriceQuote[], askQuotes: PriceQuote[], trades: Trade[]) => {
    const latestTimeStamp = Math.max(
      bidQuotes[bidQuotes.length - 1]?.timeOfEvent.timeStampMs ?? 0,
      askQuotes[askQuotes.length - 1]?.timeOfEvent.timeStampMs ?? 0,
      trades[trades.length - 1]?.timeOfEvent.timeStampMs ?? 0
    );

    const filteredBidQuotes = filterRecentEvents(bidQuotes, latestTimeStamp).slice(
      0,
      maxNrOfEvents
    );

    const earlistTimestamp: number = filteredBidQuotes[0]?.timeOfEvent.timeStampMs ?? 0;
    const filterOldEvents = (events: Event[]) => {
      return events
        .filter((event: Event) => event.timeOfEvent.timeStampMs >= earlistTimestamp)
        .slice(0, maxNrOfEvents);
    };

    return {
      bidQuotes: filteredBidQuotes,
      askQuotes: filterOldEvents(askQuotes),
      trades: filterOldEvents(trades),
    };
  };
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

    var { bidQuotes, askQuotes, trades } = filterAllEvents(
      updatedBidQuotes,
      updatedAskQuotes,
      prevSpread.trades
    );

    return {
      ...prevSpread,
      bidQuotes: bidQuotes,
      askQuotes: askQuotes,
      trades: trades,
      state: prevSpread.state,
    };
  } else if (isStateChange(message)) {
    const stateChange: StateChange = message as StateChange;

    const { bidQuotes, askQuotes, trades } = filterAllEvents(
      prevSpread.bidQuotes,
      prevSpread.askQuotes,
      prevSpread.trades
    );

    return {
      ...prevSpread,
      bidQuotes: bidQuotes,
      askQuotes: askQuotes,
      trades: trades,
      state: stateChange.tradeState,
    };
  } else if (isTrade(message)) {
    const trade: Trade = message as Trade;

    const { bidQuotes, askQuotes, trades } = filterAllEvents(
      prevSpread.bidQuotes,
      prevSpread.askQuotes,
      [...prevSpread.trades, trade]
    );

    return {
      ...prevSpread,
      bidQuotes: bidQuotes,
      askQuotes: askQuotes,
      trades: trades,
    };
  }

  return prevSpread;
};

const filterRecentEvents = <T extends Event>(events: T[], latestTimeStamp: number) => {
  return events.filter((event: Event) =>
    isWithinLastNSeconds(latestTimeStamp, event.timeOfEvent.timeStampMs, maxNrOfSeconds)
  );
};

function LiveMarketSpreadChart({ orderbook }: Props) {
  const cacheSpread = useRef<Spread>({
    orderbook: orderbook,
    state: "Inactive",
    askQuotes: [],
    bidQuotes: [],
    trades: [],
  });

  const [spread, setSpread] = useState<Spread>(cacheSpread.current);

  const handleMessage = (message: Message) => {
    cacheSpread.current = updateCacheSpread(message, cacheSpread.current);
    throttledSetSpread(cacheSpread.current);
  };

  const throttledSetSpread = useCallback(
    throttle((newSpread: Spread) => {
      setSpread(newSpread);
    }, updateFrequencyMs),
    []
  );

  useEffect(() => {
    const topics = [
      "/topic/trades/" + orderbook,
      "/topic/topOfBook/" + orderbook,
      "/topic/stateChange/" + orderbook,
    ];
    const subscription = stompSubcription({
      id: "Spread Chart",
      topics: topics,
      handleMessage: (message) => handleMessage(message),
    });
    return () => {
      subscription.unsubscribe();
    };
  }, [orderbook]);

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
