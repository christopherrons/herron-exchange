import { formatTime, isOrder, isStateChange, isTrade, isMarketOrder } from "../../common/utils";
import { Message, Order, StateChange, Trade } from "../../common/types";
import MessageTable from "../common/MessageTable";

interface Props {
  table: Table;
}

interface Table {
  orderbookId: string;
  messages: Message[];
}

const headers: string[] = ["Event", "Time"];

function OrderbookEventTable({ table }: Props) {
  const tableExtractor = (message: Message) => {
    let data: string[] = [];
    if (isOrder(message)) {
      const order: Order = message as Order;
      data.push(formatOrderDetails(order));
      data.push(formatTime(order.timeOfEvent));
    } else if (isStateChange(message)) {
      const stateChange: StateChange = message as StateChange;
      data.push(formatStateChangeDetails(stateChange));
      data.push(formatTime(stateChange.timeOfEvent));
    } else if (isTrade(message)) {
      const trade: Trade = message as Trade;
      data.push(formatTradeDetails(trade));
      data.push(formatTime(trade.timeOfEvent));
    } else {
      console.log("Message not handled: " + message);
    }

    return data;
  };

  return (
    <div>
      <MessageTable
        items={table.messages}
        columnHeaders={headers}
        heading={"Orderbook Events"}
        tableExtractor={tableExtractor}
      ></MessageTable>
    </div>
  );
}

const formatOrderDetails = (order: Order): string => {
  if (isMarketOrder(order)) {
    return `${order.orderSide} Order ${order.orderOperation} 
    ${order.currentVolume.value}@MARKET-PRICE`;
  }
  return `${order.orderSide} Order ${order.orderOperation} 
      ${order.currentVolume.value}@${order.price.value}`;
};

const formatTradeDetails = (trade: Trade): string => {
  return `Trade ${trade.volume.value}@${trade.price.value}`;
};

const formatStateChangeDetails = (stateChange: StateChange): string => {
  return `State Change ${stateChange.tradeState}`;
};

export default OrderbookEventTable;
