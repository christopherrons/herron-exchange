import { useEffect, useState } from "react";
import ScatterChart from "./ScatterChart";
import { stompSubcription } from "../common/StompClient";
import { PriceQuote, Message, StateChange, Trade, TimeOfEvent, Event } from "../common/Types";
import { isPriceQuote, isStateChange, isTrade } from "../common/Utils";

interface Summary {
  state: string;
  askQuotes: PriceQuote[];
  bidQuotes: PriceQuote[];
  trades: Trade[];
}

interface Props {
  orderbook: string;
}

const maxEvents: number = 1000;

function MarketSummaryChart({ orderbook }: Props) {
  const [summary, setSummary] = useState<Summary>({
    state: "Inactive",
    askQuotes: [],
    bidQuotes: [],
    trades: [],
  });

  const handleMessage = (message: Message) => {
    setSummary((prevSummary) => {
      const filterRecentEvents = (events: any[]) => {
        return events
          .filter((event: Event) => isWithinLastSeconds(event.timeOfEvent.timeStampMs))
          .slice(0, maxEvents);
      };

      const filterAllEvents = (
        bidQuotes: PriceQuote[],
        askQuotes: PriceQuote[],
        trades: Trade[]
      ) => {
        return {
          bidQuotes: filterRecentEvents(bidQuotes),
          askQuotes: filterRecentEvents(askQuotes),
          trades: filterRecentEvents(trades),
        };
      };

      if (isPriceQuote(message)) {
        const priceQuote: PriceQuote = message as PriceQuote;

        const adjustQuoteTimestamp = (quote: PriceQuote, newTimestamp: number): PriceQuote => ({
          ...quote,
          timeOfEvent: {
            ...quote.timeOfEvent,
            timeStampMs: newTimestamp,
          },
        });

        const addPriceGap = (bidQuotes: PriceQuote[], askQuotes: PriceQuote[]) => {
          if (bidQuotes.length === 0 || askQuotes.length === 0) {
            return {
              bidQuotes: bidQuotes,
              askQuotes: askQuotes,
            };
          }
          const latestBidQuote = bidQuotes[bidQuotes.length - 1];
          const latestAskQuote = askQuotes[askQuotes.length - 1];

          const latestBidTimestamp = latestBidQuote.timeOfEvent.timeStampMs;
          const latestAskTimestamp = latestAskQuote.timeOfEvent.timeStampMs;

          let adjustedBidQuotes = [...bidQuotes];
          let adjustedAskQuotes = [...askQuotes];

          if (latestBidTimestamp < latestAskTimestamp) {
            const newBidQuote = adjustQuoteTimestamp(latestBidQuote, latestAskTimestamp);
            adjustedBidQuotes = [newBidQuote, ...bidQuotes];
          } else if (latestBidTimestamp > latestAskTimestamp) {
            const newAskQuote = adjustQuoteTimestamp(latestAskQuote, latestBidTimestamp);
            adjustedAskQuotes = [newAskQuote, ...askQuotes];
          }
          return {
            bidQuotes: adjustedBidQuotes,
            askQuotes: adjustedAskQuotes,
          };
        };

        const processQuotes = (bidQuotes: PriceQuote[], askQuotes: PriceQuote[]) => {
          const { bidQuotes: adjustedBidQuotes, askQuotes: adjustedAskQuotes } = addPriceGap(
            bidQuotes,
            askQuotes
          );
          return {
            bidQuotes: filterRecentEvents(adjustedBidQuotes),
            askQuotes: filterRecentEvents(adjustedAskQuotes),
          };
        };

        const updatedBidQuotes =
          priceQuote.side === "BID"
            ? [priceQuote, ...prevSummary.bidQuotes]
            : prevSummary.bidQuotes;

        const updatedAskQuotes =
          priceQuote.side === "ASK"
            ? [priceQuote, ...prevSummary.askQuotes]
            : prevSummary.askQuotes;

        var { bidQuotes, askQuotes } = processQuotes(updatedBidQuotes, updatedAskQuotes);

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
          [trade, ...prevSummary.trades]
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
      "/topic/bestBid/" + orderbook,
      "/topic/bestAsk/" + orderbook,
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
    <div className="ms-summary-chart">
      <ScatterChart
        datasets={[
          {
            label: "Bid Prices",
            options: {
              backgroundColor: "rgba(44, 160, 44, 0.2)",
              borderColor: "rgba(44, 160, 44, 1)",
              showLine: true,
              stepped: true,
              fill: "start",
            },
            dataPoints: summary.bidQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
          {
            label: "Ask Prices",
            options: {
              backgroundColor: "rgba(214, 39, 40, 0.2)",
              borderColor: "rgba(214, 39, 40, 1)",
              showLine: true,
              stepped: true,
              fill: "end",
            },
            dataPoints: summary.askQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
          {
            label: "Trade Prices",
            options: {
              backgroundColor: "rgba(31, 119, 180, 0.2)",
              borderColor: "rgba(31, 119, 180, 1)",
              showLine: false,
              stepped: false,
              pointRadius: 5,
              pointStyle: "rect",
            },
            dataPoints: summary.trades.map((trade: Trade) => ({
              xValue: trade.timeOfEvent.timeStampMs,
              yValue: parseFloat(trade.price.value),
            })),
          },
        ]}
        options={{
          elements: {
            line: {
              tension: 0.5,
              stepped: true,
            },
            showline: true,
          },
          plugins: {
            title: {
              display: true,
              text: `Market Summary Chart ${orderbook} in state ${summary.state}`,
            },
          },
          scales: {
            x: {
              type: "time",
              time: {
                unit: "second",
                tooltipFormat: "ll ss:mm",
                displayFormats: {
                  second: "HH:mm:ss",
                  minute: "HH:mm",
                  hour: "HH:mm",
                  day: "MMM D",
                  week: "ll",
                  month: "MMM YYYY",
                  quarter: "[Q]Q - YYYY",
                  year: "YYYY",
                },
              },
              title: {
                display: true,
                text: "Timestamp",
              },
            },
            y: {
              title: {
                display: true,
                text: "Price",
              },
            },
          },
        }}
      />
    </div>
  );
}

const isWithinLastSeconds = (timestamp: number): boolean => {
  const now = new Date().getTime();
  const oneMinuteAgo = now - 30 * 1000;
  return timestamp >= oneMinuteAgo;
};

export default MarketSummaryChart;
