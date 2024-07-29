import { PriceQuote, Trade } from "../common/Types";
import ScatterChart from "./ScatterChart";

interface Props {
  summary: MarketSummary;
}

interface MarketSummary {
  orderbook: string;
  state: string;
  askQuotes: PriceQuote[];
  bidQuotes: PriceQuote[];
  trades: Trade[];
}

function MarketSummaryChart({ summary }: Props) {
  return (
    <div className="ms-summary-chart">
      <h3>Market Summary</h3>
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
            label: "Trades",
            options: {
              backgroundColor: "rgba(31, 119, 180, 0.2)",
              borderColor: "rgba(31, 119, 180, 1)",
              showLine: false,
              stepped: false,
              pointRadius: 3,
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
              text: `Orderbook ${summary.orderbook} in state ${summary.state}`,
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

export default MarketSummaryChart;
