import LiveMarketByLevelBarChart from "./LiveMarketByLevelBarChart";
import LiveOrderbookEventTable from "./LiveOrderbookEventTable";
import LiveSpreadChart from "./LiveSpreadChart";

function MarketSummary() {
  const orderbookId = "DE000F0HNSJ4";
  //const orderbookId = "bitstamp_equity_btcusd";
  return (
    <div className="App">
      <div className="dataCard instrumentSelector"></div>

      <div className="dataCard spreadChart">
        <div>
          <LiveSpreadChart orderbookId={orderbookId}></LiveSpreadChart>
        </div>
      </div>

      <div className="dataCard eventTable">
        <div>
          <LiveOrderbookEventTable orderbookId={orderbookId}></LiveOrderbookEventTable>
        </div>
      </div>

      <div className="dataCard marketByLevel">
        <div>
          <LiveMarketByLevelBarChart orderbookId={orderbookId}></LiveMarketByLevelBarChart>
        </div>
      </div>
    </div>
  );
}

export default MarketSummary;
