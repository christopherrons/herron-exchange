import { PriceQuote, Trade } from "../../common/types";
import ScatterChart from "../common/ScatterChart";
import {
  ASK_BACKGROUND_COLOR,
  ASK_BORDER_COLOR,
  BID_BACKGROUND_COLOR,
  BID_BORDER_COLOR,
  TRADE_BACKGROUND_COLOR,
  TRADE_BORDER_COLOR,
} from "../../common/colors";

interface Props {
  spread: Spread;
}

interface Spread {
  orderbookId: string;
  state: string;
  askQuotes: PriceQuote[];
  bidQuotes: PriceQuote[];
  trades: Trade[];
}

function SpreadChart({ spread }: Props) {
  const data = {
    datasets: [
      {
        label: "Trades",
        backgroundColor: TRADE_BACKGROUND_COLOR,
        borderColor: TRADE_BORDER_COLOR,
        showLine: false,
        stepped: false,
        pointRadius: 3,
        pointStyle: "rect",
        data: spread.trades.map((trade: Trade) => ({
          x: trade.timeOfEvent.timeStampMs,
          y: parseFloat(trade.price.value),
        })),
      },
      {
        label: "Bid Prices",
        backgroundColor: BID_BACKGROUND_COLOR,
        borderColor: BID_BORDER_COLOR,
        showLine: true,
        stepped: true,
        fill: "start",
        pointRadius: 0,
        data: spread.bidQuotes.map((quote: PriceQuote) => ({
          x: quote.timeOfEvent.timeStampMs,
          y: parseFloat(quote.price.value),
        })),
      },
      {
        label: "Ask Prices",
        backgroundColor: ASK_BACKGROUND_COLOR,
        borderColor: ASK_BORDER_COLOR,
        showLine: true,
        stepped: true,
        fill: "end",
        pointRadius: 0,
        data: spread.askQuotes.map((quote: PriceQuote) => ({
          x: quote.timeOfEvent.timeStampMs,
          y: parseFloat(quote.price.value),
        })),
      },
    ],
  };

  const options = {
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
        text: `Orderbook in state ${spread.state}`,
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
  };
  return (
    <div className="ms-spread-chart">
      <h3>Bid Ask Spread - {spread.orderbookId}</h3>
      <ScatterChart data={data} options={options} />
    </div>
  );
}

export default SpreadChart;
