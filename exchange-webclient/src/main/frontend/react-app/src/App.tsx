import "./App.css";
import LiveSpreadChart from "./components/LiveSpreadChart";
import LiveOrderbookEventTable from "./components/LiveOrderbookEventTable";

function App() {
  //const orderbook = "DE000F0HNSJ4";
  const orderbook = "bitstamp_equity_btcusd";
  return (
    <div className="App">
      <div className="dataCard instrumentSelector"></div>
      <div className="dataCard spreadChart">
        <div>
          <LiveSpreadChart orderbook={orderbook}></LiveSpreadChart>
        </div>
      </div>

      <div className="dataCard eventTable">
        <div>
          <LiveOrderbookEventTable orderbook={orderbook}></LiveOrderbookEventTable>
        </div>
      </div>
      <div className="dataCard marketByLevel"></div>
    </div>
  );
}

export default App;
