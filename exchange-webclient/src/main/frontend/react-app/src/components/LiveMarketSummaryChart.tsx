import { useEffect, useState } from "react";
import { stompSubcription } from "../common/StompClient";
import { PriceQuote, Message, StateChange, Trade, Event, TopOfBook } from "../common/Types";
import { formatTime, isStateChange, isTopOfBook, isTrade } from "../common/Utils";
import MarketSummaryChart from "./MarketSummaryChart";

interface Summary {
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
const maxNrOfSeconds: number = 60;

function LiveMarketSummaryChart({ orderbook }: Props) {
  const [summary, setSummary] = useState<Summary>({
    orderbook: orderbook,
    state: "Inactive",
    askQuotes: [],
    bidQuotes: [],
    trades: [],
  });

  const handleMessage = (message: Message) => {
    setSummary((prevSummary) => {
      const filterRecentEvents = (events: any[], latestTimeStamp: number) => {
        var e: Event[] = events as Event[];
        return events
          .filter((event: Event) =>
            isWithinLastNSeconds(latestTimeStamp, event.timeOfEvent.timeStampMs, maxNrOfSeconds)
          )
          .slice(0, maxNrOfEvents);
      };

      const filterAllEvents = (
        bidQuotes: PriceQuote[],
        askQuotes: PriceQuote[],
        trades: Trade[]
      ) => {
        var latestBidQuoteTimeStamp = bidQuotes[bidQuotes.length - 1]?.timeOfEvent.timeStampMs ?? 0;
        var latestAskQuoteTimeStamp = askQuotes[askQuotes.length - 1]?.timeOfEvent.timeStampMs ?? 0;
        var latestTradeQuoteTimeStamp = trades[trades.length - 1]?.timeOfEvent.timeStampMs ?? 0;
        var latestTimeStamp = Math.max(
          latestBidQuoteTimeStamp,
          latestAskQuoteTimeStamp,
          latestTradeQuoteTimeStamp
        );
        return {
          bidQuotes: filterRecentEvents(bidQuotes, latestTimeStamp),
          askQuotes: filterRecentEvents(askQuotes, latestTimeStamp),
          trades: filterRecentEvents(trades, latestTimeStamp),
        };
      };
      if (isTopOfBook(message)) {
        const topOfBook: TopOfBook = message as TopOfBook;

        const updatedBidQuotes =
          topOfBook.bidQuote?.quoteType === "BID_PRICE"
            ? [...prevSummary.bidQuotes, topOfBook.bidQuote]
            : prevSummary.bidQuotes;

        const updatedAskQuotes =
          topOfBook.askQuote?.quoteType === "ASK_PRICE"
            ? [...prevSummary.askQuotes, topOfBook.askQuote]
            : prevSummary.askQuotes;

        var { bidQuotes, askQuotes } = filterAllEvents(
          updatedBidQuotes,
          updatedAskQuotes,
          prevSummary.trades
        );

        return {
          ...prevSummary,
          bidQuotes: bidQuotes,
          askQuotes: askQuotes,
          trades: prevSummary.trades,
          stateChanges: prevSummary.state,
        };
      } else if (isStateChange(message)) {
        const stateChange: StateChange = message as StateChange;

        const { bidQuotes, askQuotes, trades } = filterAllEvents(
          prevSummary.bidQuotes,
          prevSummary.askQuotes,
          prevSummary.trades
        );

        return {
          ...prevSummary,
          bidQuotes: bidQuotes,
          askQuotes: askQuotes,
          trades: trades,
          state: stateChange.tradeState,
        };
      } else if (isTrade(message)) {
        const trade: Trade = message as Trade;

        const { bidQuotes, askQuotes, trades } = filterAllEvents(
          prevSummary.bidQuotes,
          prevSummary.askQuotes,
          [...prevSummary.trades, trade]
        );

        return {
          ...prevSummary,
          bidQuotes: bidQuotes,
          askQuotes: askQuotes,
          trades: trades,
        };
      }

      return prevSummary;
    });
  };

  useEffect(() => {
    const topics = [
      "/topic/trades/" + orderbook,
      "/topic/topOfBook/" + orderbook,
      "/topic/stateChange/" + orderbook,
    ];
    const subscription = stompSubcription({
      id: "Market Summary Chart",
      topics: topics,
      handleMessage: (message) => handleMessage(message),
    });
    return () => {
      subscription.unsubscribe();
    };
  }, [orderbook]);

  return (
    <div>
      <MarketSummaryChart summary={summary}></MarketSummaryChart>
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

export default LiveMarketSummaryChart;
