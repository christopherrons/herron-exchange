import { useEffect, useState } from "react";
import { formatTime, isOrder, isStateChange, isTradeExecution, isTrade } from "../common/Utils";
import { Message, Order, StateChange, Trade } from "../common/Types";
import { stompSubcription } from "../common/StompClient";
import MessageTable from "./MessageTable";

interface Props {
  orderbook: string;
}

const headers: string[] = ["Event"];

function EventWebSocketTable({ orderbook }: Props) {
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    const topic = "/topic/orderbookEvent/" + orderbook;
    stompSubcription({
      topic: topic,
      handleMessage: (message) =>
        setMessages((prevMessages) => {
          const newMessages = [message, ...prevMessages];
          return newMessages.slice(0, 15);
        }),
    });
  }, [orderbook]);

  const tableExtractor = (message: Message) => {
    let cell: string = "";
    if (isOrder(message)) {
      const order: Order = message;
      cell = formatOrderDetails(order);
    } else if (isStateChange(message)) {
      const stateChange: StateChange = message;
      cell = formatStateChangeDetails(stateChange);
    } else if (isTrade(message)) {
      const trade: Trade = message;
      cell = formatTradeDetails(trade);
    } else {
      console.log(message);
    }

    return [cell];
  };

  return (
    <div>
      <MessageTable
        items={messages}
        columnHeaders={headers}
        heading="Trading Engine Events"
        tableExtractor={tableExtractor}
      ></MessageTable>
    </div>
  );
}

const formatOrderDetails = (order: Order): string => {
  return `${order.orderSide} Order ${order.orderOperation} 
      ${order.currentVolume.value}@${order.price.value} ${formatTime(order.timeOfEvent)}`;
};

const formatTradeDetails = (trade: Trade): string => {
  return `Trade ${trade.volume.value}@${trade.price.value} ${formatTime(trade.timeOfEvent)}`;
};

const formatStateChangeDetails = (stateChange: StateChange): string => {
  return `State Change ${stateChange.tradeState} ${formatTime(stateChange.timeOfEvent)}`;
};

export default EventWebSocketTable;
