import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { Message } from "./Types";

interface Props {
  topics: string[];
  handleMessage: (message: Message) => void;
}

export const stompSubcription = ({ topics, handleMessage }: Props) => {
  const sockJS = new SockJS("http://localhost:8087/exchange");
  const client = new Client({
    webSocketFactory: () => sockJS,
    onConnect: () => {
      console.log("Connected to STOMP broker");

      topics.forEach((topic: string) => {
        client.subscribe(topic, (message: IMessage) => {
          console.log("Subscribe to topic" + topic);
          const messageBody: Message = JSON.parse(message.body);
          handleMessage(messageBody);
        });
      })
    },
    onDisconnect: () => {
      console.log("Disconnected from STOMP broker");
    },
    onStompError: (error: any) => {
      console.error("STOMP error", error);
    },
  });

  client.activate();

  return () => {
    client.deactivate();
  };
};
