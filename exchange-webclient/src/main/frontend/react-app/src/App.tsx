import "./App.css";
import LiveMarketByLevelBarChart from "./components/market-summary/LiveMarketByLevelBarChart";
import LiveOrderbookEventTable from "./components/market-summary/LiveOrderbookEventTable";
import LiveSpreadChart from "./components/market-summary/LiveSpreadChart";
import MarketSummary from "./components/market-summary/MarketSummary";

function App() {
  //const orderbookId = "DE000F0HNSJ4";
  const orderbookId = "bitstamp_equity_btcusd";
  return (
    <div className="App">
      <div>
        <div>
          <MarketSummary></MarketSummary>
        </div>
      </div>
    </div>
  );
}

export default App;
