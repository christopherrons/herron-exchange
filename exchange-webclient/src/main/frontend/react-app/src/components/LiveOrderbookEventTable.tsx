import { useCallback, useEffect, useRef, useState } from "react";
import { Message } from "../common/Types";
import { stompSubcription } from "../common/StompClient";
import OrderbookEventTable from "./OrderbookEventTable";
import { throttle } from "lodash";

interface Props {
  orderbook: string;
}

interface Table {
  orderbook: string;
  messages: Message[];
}

const updateFrequencyMs: number = 1000;

function LiveOrderbookEventTable({ orderbook }: Props) {
  const cacheTable = useRef<Table>({ orderbook: orderbook, messages: [] });
  const [table, setTable] = useState<Table>(cacheTable.current);

  const throttledSetTable = useCallback(
    throttle((newTable: Table) => {
      setTable({
        ...newTable,
        messages: newTable.messages.slice(0, 15),
      });
    }, updateFrequencyMs),
    []
  );

  const handleMessage = (message: Message) => {
    cacheTable.current = {
      ...cacheTable.current,
      messages: [message, ...cacheTable.current.messages],
    };
    throttledSetTable(cacheTable.current);
  };

  useEffect(() => {
    const topic = "/topic/orderbookEvent/" + orderbook;
    const subscription = stompSubcription({
      id: "Event table",
      topics: [topic],
      handleMessage: (message) => handleMessage(message),
    });
    return () => {
      subscription.unsubscribe();
    };
  }, [orderbook]);

  return (
    <div>
      <OrderbookEventTable table={table}></OrderbookEventTable>
    </div>
  );
}

export default LiveOrderbookEventTable;
