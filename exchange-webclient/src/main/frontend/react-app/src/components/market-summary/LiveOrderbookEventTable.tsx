import { useCallback, useEffect, useRef, useState } from "react";
import { Message } from "../../common/types";
import { stompSubcription } from "../../common/stomp-client";
import { throttle } from "lodash";
import OrderbookEventTable from "./OrderbookEventTable";

interface Props {
  orderbookId: string;
}

interface Table {
  orderbookId: string;
  messages: Message[];
}

const updateFrequencyMs: number = 1000;

const initCache = (orderbookId: string) => {
  return { orderbookId: orderbookId, messages: [] };
};

function LiveOrderbookEventTable({ orderbookId }: Props) {
  const cacheTable = useRef<Table>(initCache(orderbookId));
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
    cacheTable.current = initCache(orderbookId);
    setTable(cacheTable.current);
    const topic = "/topic/orderbookEvent/" + orderbookId;
    const subscription = stompSubcription({
      id: "Event table",
      topics: [topic],
      handleMessage: (message) => handleMessage(message),
    });
    return () => {
      subscription.unsubscribe();
    };
  }, [orderbookId]);

  return (
    <div>
      <OrderbookEventTable table={table}></OrderbookEventTable>
    </div>
  );
}

export default LiveOrderbookEventTable;
