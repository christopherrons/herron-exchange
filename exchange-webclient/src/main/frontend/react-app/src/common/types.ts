export interface Message {
  "@type": string;
}

export interface Event extends Message {
  eventType: string;
  timeOfEvent: TimeOfEvent;
}

export interface OrderbookEvent extends Event {
  orderbookId: string;
}

export interface TradeExecution extends OrderbookEvent {
  messages: OrderbookEvent[];
}

export interface Order extends OrderbookEvent {
  orderId: string;
  instrumentId: string;
  participant: Participant;
  orderSide: string;
  orderOperation: string;
  initialVolume: Volume;
  currentVolume: Volume;
  price: Price;
  orderOperationCause: string;
  timeInForce: string;
  orderType: string;
}

export interface LimitOrder extends Order {}

export interface StateChange extends OrderbookEvent {
  tradeState: string;
}

export interface Trade extends OrderbookEvent {
  tradeId: string;
  bidOrderId: string;
  askOrderId: string;
  instrumentId: string;
  isBidSideAggressor: boolean;
  bidParticipant: Participant;
  askParticipant: Participant;
  tradeType: string;
  volume: Volume;
  price: Price;
}

export interface TopOfBook extends OrderbookEvent {
  lastPrice?: PriceQuote;
  askQuote?: PriceQuote;
  bidQuote?: PriceQuote;

}
export interface Quote extends OrderbookEvent {
  quoteType: string;
}

export interface PriceQuote extends Quote {
  price: Price;
}

export interface MarketByLevel extends OrderbookEvent {
  levelData: LevelData[];
}

export interface LevelData {
  level: number;
  nrOfAskOrders: number;
  nrOfBidOrders: number;
  totalNrOfOrders: number;
  totalVolume?: Volume;
  bidVolume?: Volume;
  askVolume?: Volume;
  askPrice?: Price;
  bidPrice?: Price;
}

export interface TimeOfEvent {
  timeStampMs: number;
  zoneId: string;
}

export interface Member {
  memberId: string;
}

export interface User {
  fullName: string;
}

export interface Participant {
  member: Member;
  user: User;
  participantId: string;
}

export interface Volume {
  value: string;
}

export interface Price {
  value: string;
}

export interface InstrumentHierarchy extends Message {
  timeStamp: TimeOfEvent;
  instrumentTree: Tree;
}

export interface Tree {
  rootNode: TreeNode;
}

export interface TreeNode {
  id: string;
  name: string;
  children?: TreeNode[];
};