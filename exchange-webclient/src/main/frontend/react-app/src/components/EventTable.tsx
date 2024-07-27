import { useEffect, useState } from "react";
import { formatTime, isOrder, isStateChange, isTradeExecution, isTrade } from "../common/Utils";
import { Message, Order, StateChange, Trade } from "../common/Types";
import { stompSubcription } from "../common/StompClient";
import MessageTable from "./MessageTable";

interface Props {
  orderbook: string;
}

const headers: string[] = ["Event", "Time"];

function EventTable({ orderbook }: Props) {
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    const topic = "/topic/orderbookEvent/" + orderbook;
    stompSubcription({
      topics: [topic],
      handleMessage: (message) =>
        setMessages((prevMessages) => {
          const newMessages = [message, ...prevMessages];
          return newMessages.slice(0, 15);
        }),
    });
  }, [orderbook]);

  const tableExtractor = (message: Message) => {
    let data: string[] = [];
    if (isOrder(message)) {
      const order: Order = message;
      data.push(formatOrderDetails(order));
      data.push(formatTime(order.timeOfEvent));
    } else if (isStateChange(message)) {
      const stateChange: StateChange = message;
      data.push(formatStateChangeDetails(stateChange));
      data.push(formatTime(stateChange.timeOfEvent));
    } else if (isTrade(message)) {
      const trade: Trade = message;
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
      ${order.currentVolume.value}@${order.price.value}`;
};

const formatTradeDetails = (trade: Trade): string => {
  return `Trade ${trade.volume.value}@${trade.price.value}`;
};

const formatStateChangeDetails = (stateChange: StateChange): string => {
  return `State Change ${stateChange.tradeState}`;
};

export default EventTable;
