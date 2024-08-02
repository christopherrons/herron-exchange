import { defaults } from "chart.js/auto";
import { Scatter } from "react-chartjs-2";
import { Chart, ChartData, ChartOptions, registerables } from "chart.js";
import "chartjs-adapter-moment";

Chart.register(...registerables);

interface Props {
  data: ChartData<any, any, any>;
  options?: ChartOptions<any>;
}

function ScatterChart({ data, options }: Props) {
  defaults.maintainAspectRatio = false;
  defaults.responsive = true;
  defaults.plugins.title.display = true;
  defaults.plugins.title.align = "start";
  defaults.plugins.title.color = "black";
  return <Scatter data={data} options={options} />;
}

export default ScatterChart;
