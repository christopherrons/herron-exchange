package com.herron.exchange.exchangeengine.server.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class ExchangeStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeStompSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("Connected to WebSocket server");
        session.subscribe("/topic/messages", this);
      //  session.send("/app/chat", "Hello, WebSocket!");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Error handling WebSocket message", exception);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("Received message: " + payload);
    }
}
