import { PriceQuote, Trade } from "../common/Types";
import ScatterChart from "./ScatterChart";

interface Props {
  spread: Spread;
}

interface Spread {
  orderbook: string;
  state: string;
  askQuotes: PriceQuote[];
  bidQuotes: PriceQuote[];
  trades: Trade[];
}

function SpreadChart({ spread }: Props) {
  return (
    <div className="ms-spread-chart">
      <h3>Bid Ask Spread</h3>
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
              pointRadius: 0,
            },
            dataPoints: spread.bidQuotes.map((quote: PriceQuote) => ({
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
              pointRadius: 0,
            },
            dataPoints: spread.askQuotes.map((quote: PriceQuote) => ({
              xValue: quote.timeOfEvent.timeStampMs,
              yValue: parseFloat(quote.price.value),
            })),
          },
          {
            label: "Trades",
            options: {
              backgroundColor: "rgba(31, 119, 180, 0.2)",
              borderColor: "rgba(31, 119, 180, 1)",
              showLine: false,
              stepped: false,
              pointRadius: 3,
              pointStyle: "rect",
            },
            dataPoints: spread.trades.map((trade: Trade) => ({
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
              text: `Orderbook ${spread.orderbook} in state ${spread.state}`,
            },
          },
          scales: {
            x: {
              type: "time",
              time: {
                unit: "second",
                tooltipFormat: "HH:mm:ss",
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

export default SpreadChart;
