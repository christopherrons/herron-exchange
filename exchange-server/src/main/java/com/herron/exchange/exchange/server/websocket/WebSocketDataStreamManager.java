package com.herron.exchange.exchange.server.websocket;


import com.herron.exchange.common.api.common.api.trading.orders.Order;
import com.herron.exchange.common.api.common.api.trading.trades.Trade;
import com.herron.exchange.common.api.common.enums.WebSocketTopicEnum;
import com.herron.exchange.exchange.server.websocket.model.DataStream;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebSocketDataStreamManager {
    private final SimpMessagingTemplate messagingTemplate;
    private static final String BASE_TOPIC = "/topic/";

    public WebSocketDataStreamManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishOrder(long sequenceNumber, Order order) {
        publishData(WebSocketTopicEnum.ORDER_STREAM.getTopicName(), new DataStream(sequenceNumber, order));
    }

    public void publishTrade(long sequenceNumber, Trade trade) {
        publishData(WebSocketTopicEnum.TRADE_STREAM.getTopicName(), new DataStream(sequenceNumber, trade));
    }

    private void publishData(String endPoint, DataStream dataStream) {
        if (!dataStream.isEmpty()) {
            messagingTemplate.convertAndSend(BASE_TOPIC + "test", "dataStream");
        }
    }
}
