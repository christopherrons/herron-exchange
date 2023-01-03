package com.herron.exchange.exchange.server.websocket.model;

import com.herron.exchange.common.api.common.api.Message;

public record DataStream(long sequenceNumber, Message message) {

    public boolean isEmpty() {
        return message == null;
    }
}
