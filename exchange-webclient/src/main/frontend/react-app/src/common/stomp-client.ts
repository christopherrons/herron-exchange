import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { Message } from "./types";
import { useEffect } from "react";

interface Props {
  id: string;
  topics: string[];
  handleMessage: (message: Message) => void;
}

export const stompSubcription = ({ id, topics, handleMessage }: Props) => {
  const sockJS = new SockJS("http://localhost:8087/exchange");
  const client = new Client({
    webSocketFactory: () => sockJS,
    onConnect: () => {
      console.log(id + " connected to STOMP broker");

      topics.forEach((topic: string) => {
        console.log(id + " subscribed to topic " + topic);
        client.subscribe(topic, (message: IMessage) => {
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

  return {
    unsubscribe: () => {
      client.deactivate();
    },
  };
};
