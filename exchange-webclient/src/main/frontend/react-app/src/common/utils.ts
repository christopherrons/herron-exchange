import { Message, TimeOfEvent } from "./types";

export const isTradeExecution = (message: Message): boolean => {
  return message["@type"] === "TREX";
};

export const isTrade = (message: Message): boolean => {
  return message["@type"] === "TRAD";
};

export const isMarketByLevel = (message: Message): boolean => {
  return message["@type"] === "MBLE";
};

export const isOrder = (message: Message): boolean => {
  return message["@type"] === "LIOR" || message["@type"] === "MAOR";
};

export const isLimitOrder = (message: Message): boolean => {
  return message["@type"] === "LIOR";
};

export const isMarketOrder = (message: Message): boolean => {
  return message["@type"] === "MAOR";
};

export const isStateChange = (message: Message): boolean => {
  return message["@type"] === "STCH";
};

export const isPriceQuote = (message: Message): boolean => {
  return message["@type"] === "PRQU";
};

export const isTopOfBook = (message: Message): boolean => {
  return message["@type"] === "TOOB";
};

export const formatTime = (event: TimeOfEvent): string => {
  const { timeStampMs, zoneId } = event;

  const date = new Date(timeStampMs);

  const options: Intl.DateTimeFormatOptions = {
    timeZone: zoneId,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  };

  return date.toLocaleString("en-GB", options);
};
