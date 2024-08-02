import BarChart from "../common/BarChart";
import {
  ASK_BACKGROUND_COLOR,
  ASK_BORDER_COLOR,
  BID_BACKGROUND_COLOR,
  BID_BORDER_COLOR,
} from "../../common/colors";
import { LevelData, MarketByLevel } from "../../common/types";

interface Props {
  marketByLevel: MarketByLevel;
}

function MarketByLevelBarChart({ marketByLevel }: Props) {
  const levelData: LevelData[] = marketByLevel.levelData;
  levelData.sort((a: LevelData, b: LevelData) => a.level - b.level);
  const labels = levelData.map((level: LevelData) => "Price Level " + level.level);
  const data = {
    labels: labels,
    datasets: [
      {
        label: "Bid Volume",
        data: levelData
          .filter((level: LevelData) => level.bidVolume !== undefined)
          .map((level: LevelData) => level.bidVolume?.value),
        backgroundColor: BID_BACKGROUND_COLOR,
        borderColor: BID_BORDER_COLOR,
        borderWidth: 1,
        stack: "Stack 0",
        bidPrices: levelData
          .filter((level: LevelData) => level.bidPrice !== undefined)
          .map((level: LevelData) => level.bidPrice?.value),
        nrOfBidOrders: levelData
          .filter((level: LevelData) => level.nrOfBidOrders !== undefined)
          .map((level: LevelData) => level.nrOfBidOrders),
      },
      {
        label: "Ask Volume",
        data: levelData
          .filter((level: LevelData) => level.askVolume !== undefined)
          .map((level: LevelData) => level.askVolume?.value),
        backgroundColor: ASK_BACKGROUND_COLOR,
        borderColor: ASK_BORDER_COLOR,
        borderWidth: 1,
        stack: "Stack 1",
        askPrices: levelData
          .filter((level: LevelData) => level.askPrice !== undefined)
          .map((level: LevelData) => level.askPrice?.value),
        nrOfAskOrders: levelData
          .filter((level: LevelData) => level.nrOfAskOrders !== undefined)
          .map((level: LevelData) => level.nrOfAskOrders),
      },
    ],
  };

  const options = {
    indexAxis: "y" as const,
    scales: {
      x: {
        beginAtZero: true,
        stacked: true,
        title: {
          display: true,
          text: "Volume",
        },
        ticks: {
          callback: function (value: number) {
            return Math.abs(value);
          },
        },
      },
      y: {
        stacked: true,
      },
    },
    plugins: {
      title: {
        display: true,
        text: `Top ${levelData.length} price levels`,
      },
      tooltip: {
        callbacks: {
          label: function (context: any) {
            let label = context.dataset.label || "";
            if (label) {
              label += ": ";
            }
            label += Math.abs(context.raw);
            const price =
              context.dataset.label === "Bid Volume"
                ? context.dataset.bidPrices[context.dataIndex]
                : context.dataset.askPrices[context.dataIndex];
            const nrOfOrders =
              context.dataset.label === "Bid Volume"
                ? context.dataset.nrOfBidOrders[context.dataIndex]
                : context.dataset.nrOfAskOrders[context.dataIndex];
            return `${label} (Price: ${price}) (# Orders: ${nrOfOrders})`;
          },
        },
      },
    },
  };
  return (
    <div className="mbl-chart">
      <h3>Market by Level</h3>
      <BarChart data={data} options={options}></BarChart>
    </div>
  );
}

export default MarketByLevelBarChart;
