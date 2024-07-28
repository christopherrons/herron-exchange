import { useEffect, useState } from "react";
import ScatterChart from "./ScatterChart";
import { stompSubcription } from "../common/StompClient";
import { PriceQuote, Message, StateChange, Trade } from "../common/Types";
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

const maxEvents: number = 100;

function MarketSummaryChart({ orderbook }: Props) {
  const [summary, setSummary] = useState<Summary>({
    state: "Inactive",
    askQuotes: [],
    bidQuotes: [],
    trades: [],
  });

  const handleMessage = (message: Message) => {
    if (isPriceQuote(message)) {
      const priceQuote: PriceQuote = message;
      setSummary((prevSummary) => {
        if (priceQuote.side === "BID") {
          return {
            ...prevSummary,
            bidQuotes: [priceQuote, ...prevSummary.bidQuotes].slice(0, maxEvents),
          };
        } else {
          return {
            ...prevSummary,
            askQuotes: [priceQuote, ...prevSummary.askQuotes].slice(0, maxEvents),
          };
        }
      });
    } else if (isStateChange(message)) {
      const stateChange: StateChange = message;
      setSummary((prevSummary) => ({
        ...prevSummary,
        state: stateChange.tradeState,
      }));
    } else if (isTrade(message)) {
      const trade: Trade = message;
      setSummary((prevSummary) => {
        return {
          ...prevSummary,
          trades: [trade, ...prevSummary.trades].slice(0, maxEvents),
        };
      });
    }
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
              backgroundColor: "#FF0000",
              borderColor: "#FF0000",
              showLine: true,
              stepped: true,
            },
            dataPoints: summary.bidQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
          {
            label: "Ask Prices",
            options: {
              backgroundColor: "#064FF0",
              borderColor: "#064FF0",
              showLine: true,
              stepped: true,
            },
            dataPoints: summary.askQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
          {
            label: "Trade Prices",
            options: {
              backgroundColor: "#008000",
              borderColor: "#008000",
              showLine: false,
              stepped: false,
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

export default MarketSummaryChart;
