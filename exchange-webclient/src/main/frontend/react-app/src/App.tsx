import EventTable from "./components/EventTable";
import "./App.css";
import MarketSummaryChart from "./components/MarketSummaryChart";

function App() {
  //const orderbook = "DE000F0HNSJ4";
  const orderbook = "bitstamp_equity_btcusd";
  return (
    <div className="App">
      <div className="dataCard marketSummaryChart">
        <div>
          <MarketSummaryChart orderbook={orderbook}></MarketSummaryChart>
        </div>
      </div>

      <div className="dataCard eventTable">
        <div>
          <EventTable orderbook={orderbook}></EventTable>
        </div>
      </div>
      <div className="dataCard marketByLevel"></div>
    </div>
  );
}

export default App;
