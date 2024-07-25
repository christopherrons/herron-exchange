package com.herron.exchange.exchangeengine.server.websocket;

import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.common.api.common.messages.trading.PriceQuote;
import com.herron.exchange.common.api.common.messages.trading.TradeExecution;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static com.herron.exchange.exchangeengine.server.websocket.ExchangeWebsocketTopics.*;

public class TradingEventStreamingService {
    private final SimpMessagingTemplate messagingTemplate;

    public TradingEventStreamingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void streamOrderbookEvents(OrderbookEvent orderbookEvent) {
        switch (orderbookEvent) {
            case PriceQuote priceQuote -> streamTopOfBook(priceQuote);
            case TradeExecution tradeExecution ->
                    tradeExecution.messages().forEach(obEvent -> messagingTemplate.convertAndSend(String.format("/topic/%s/%s", ORDERBOOK_EVENT.getTopicName(), obEvent.orderbookId()), obEvent));
            default ->
                    messagingTemplate.convertAndSend(String.format("/topic/%s/%s", ORDERBOOK_EVENT.getTopicName(), orderbookEvent.orderbookId()), orderbookEvent);
        }
    }

    public void streamTopOfBook(PriceQuote quote) {
        switch (quote.side()) {
            case BID -> messagingTemplate.convertAndSend(String.format("/topic/%s/%s", BEST_BID.getTopicName(), quote.orderbookId()), quote.price());
            case ASK -> messagingTemplate.convertAndSend(String.format("/topic/%s/%s", BEST_ASK.getTopicName(), quote.orderbookId()), quote.price());
        }
    }
}
