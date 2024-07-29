import OrderbookEventTable from "./components/OrderbookEventTable";
import "./App.css";
import MarketSummaryChart from "./components/LiveMarketSummaryChart";

function App() {
  //const orderbook = "DE000F0HNSJ4";
  const orderbook = "bitstamp_equity_btcusd";
  return (
    <div className="App">
      <div className="dataCard instrumentSelector"></div>
      <div className="dataCard marketSummaryChart">
        <div>
          <MarketSummaryChart orderbook={orderbook}></MarketSummaryChart>
        </div>
      </div>

      <div className="dataCard eventTable">
        <div>
          <OrderbookEventTable orderbook={orderbook}></OrderbookEventTable>
        </div>
      </div>
      <div className="dataCard marketByLevel"></div>
    </div>
  );
}

export default App;
