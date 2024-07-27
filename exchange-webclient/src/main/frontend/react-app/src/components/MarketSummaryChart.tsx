import { useEffect, useState } from "react";
import ScatterChart from "./ScatterChart";
import { stompSubcription } from "../common/StompClient";
import { PriceQuote, Message, StateChange } from "../common/Types";
import { isPriceQuote, isStateChange } from "../common/Utils";
interface Props {
  orderbook: string;
}
const maxEvents: number = 1000;

function MarketSummaryChart({ orderbook }: Props) {
  const [state, setState] = useState<string>("Inactive");
  const [askQuotes, setAskQuotes] = useState<PriceQuote[]>([]);
  const [bidQuotes, setBidQuotes] = useState<PriceQuote[]>([]);

  const handleMessage = (message: Message) => {
    if (isPriceQuote(message)) {
      const priceQuote: PriceQuote = message;
      if (priceQuote.side === "BID") {
        setBidQuotes((previousQuotes) => {
          const quotes = [priceQuote, ...previousQuotes];
          return quotes.slice(0, maxEvents);
        });
      } else {
        setAskQuotes((previousQuotes) => {
          const quotes = [priceQuote, ...previousQuotes];
          return quotes.slice(0, maxEvents);
        });
      }
    } else if (isStateChange(message)) {
      const stateChange: StateChange = message;
      setState(stateChange.tradeState);
    }
  };

  useEffect(() => {
    const topics = [
      "/topic/bestBid/" + orderbook,
      "/topic/bestAsk/" + orderbook,
      "/topic/stateChange/" + orderbook,
    ];
    stompSubcription({
      topics: topics,
      handleMessage: (message) => handleMessage(message),
    });
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
            },
            dataPoints: bidQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
          {
            label: "Ask Prices",
            options: {
              backgroundColor: "#064FF0",
              borderColor: "#064FF0",
            },
            dataPoints: askQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
        ]}
        options={{
          showLine: true,
          elements: {
            line: {
              tension: 0.5,
            },
          },
          plugins: {
            title: {
              display: true,
              text: `Market Summary Chart ${orderbook} in state ${state}`,
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
