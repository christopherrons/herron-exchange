import { useState } from "react";
import LiveMarketByLevelBarChart from "./LiveMarketByLevelBarChart";
import LiveOrderbookEventTable from "./LiveOrderbookEventTable";
import LiveSpreadChart from "./LiveSpreadChart";
import InstrumentHierarchyBuilder from "../common/InstrumentHierarchyBuilder";
import { TreeNode } from "../../common/types";

function MarketSummary() {
  const [orderbookId, setOrderbookId] = useState("bitstamp_equity_btcusd");

  const handleItemSelect = (item: TreeNode) => {
    setOrderbookId(item.name);
  };

  return (
    <div className="market-summary">
      <div className="dataCard instrumentSelector">
        <InstrumentHierarchyBuilder onItemSelect={handleItemSelect}></InstrumentHierarchyBuilder>
      </div>
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
