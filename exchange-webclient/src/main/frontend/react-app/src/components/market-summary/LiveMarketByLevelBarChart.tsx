import { throttle } from "lodash";
import MarketByLevelBarChart from "./MarketByLevelBarChart";
import { useCallback, useEffect, useRef, useState } from "react";
import { stompSubcription } from "../../common/stomp-client";
import { MarketByLevel, Message } from "../../common/types";
import { isMarketByLevel } from "../../common/utils";

interface Props {
  orderbookId: string;
}

const updateFrequencyMs: number = 1000;

function LiveMarketByLevelBarChart({ orderbookId }: Props) {
  const cachedMarketByLevel = useRef<MarketByLevel>({
    orderbookId: orderbookId,
    levelData: [],
    timeOfEvent: { timeStampMs: 0, zoneId: "UTC" },
    eventType: "SYSTEM",
    "@type": "MBLE",
  });

  const [marketByLevel, setMarketByLevel] = useState<MarketByLevel>(cachedMarketByLevel.current);

  const handleMessage = (message: Message) => {
    if (isMarketByLevel(message)) {
      cachedMarketByLevel.current = message as MarketByLevel;
      throttledSetMarketByLevel(cachedMarketByLevel.current);
    }
  };

  const throttledSetMarketByLevel = useCallback(
    throttle((newMarketByLevel: MarketByLevel) => {
      setMarketByLevel(newMarketByLevel);
    }, updateFrequencyMs),
    []
  );

  useEffect(() => {
    const topics = ["/topic/marketByLevel/" + orderbookId];
    const subscription = stompSubcription({
      id: "Market by Level Chart",
      topics: topics,
      handleMessage: (message) => handleMessage(message),
    });
    return () => {
      subscription.unsubscribe();
    };
  }, [orderbookId]);

  return (
    <div>
      <MarketByLevelBarChart marketByLevel={marketByLevel}></MarketByLevelBarChart>
    </div>
  );
}

export default LiveMarketByLevelBarChart;
