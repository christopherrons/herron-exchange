import { Chart as ChartJS, defaults } from "chart.js/auto";
import { Scatter } from "react-chartjs-2";
import { Chart, ChartOptions, registerables } from "chart.js";
import "chartjs-adapter-moment";

Chart.register(...registerables);

interface Dataset {
  label: string;
  dataPoints: DataPoint[];
  options?: DatasetOptions;
}

interface DataPoint {
  yValue: number;
  xValue: number;
}

interface DatasetOptions {
  backgroundColor: string;
  borderColor: string;
  showLine?: boolean;
  stepped?: boolean;
}

interface Props {
  datasets: Dataset[];
  options?: ChartOptions<any>;
}

function ScatterChart({ datasets, options }: Props) {
  defaults.maintainAspectRatio = false;
  defaults.responsive = true;
  defaults.plugins.title.display = true;
  defaults.plugins.title.align = "start";
  defaults.plugins.title.color = "black";
  return (
    <Scatter
      data={{
        datasets: datasets.map((dataset: Dataset) => ({
          label: dataset.label,
          data: dataset.dataPoints.map((dp: DataPoint) => ({
            x: dp.xValue,
            y: dp.yValue,
          })),
          backgroundColor: dataset.options?.backgroundColor ?? "#064FF0",
          borderColor: dataset.options?.borderColor ?? "#064FF0",
          showLine: dataset.options?.showLine ?? false,
          stepped: dataset.options?.stepped ?? false,
        })),
      }}
      options={options}
    />
  );
}

export default ScatterChart;
