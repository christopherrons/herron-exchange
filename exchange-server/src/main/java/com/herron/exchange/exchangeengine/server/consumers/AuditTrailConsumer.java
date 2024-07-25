package com.herron.exchange.exchangeengine.server.consumers;

import com.herron.exchange.common.api.common.api.Message;
import com.herron.exchange.common.api.common.api.kafka.KafkaMessageHandler;
import com.herron.exchange.common.api.common.api.trading.OrderbookEvent;
import com.herron.exchange.common.api.common.consumer.DataConsumer;
import com.herron.exchange.common.api.common.kafka.KafkaConsumerClient;
import com.herron.exchange.common.api.common.kafka.model.KafkaSubscriptionDetails;
import com.herron.exchange.common.api.common.kafka.model.KafkaSubscriptionRequest;
import com.herron.exchange.common.api.common.messages.BroadcastMessage;
import com.herron.exchange.common.api.common.messages.common.DataStreamState;
import com.herron.exchange.exchangeengine.server.ExchangeEngine;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class AuditTrailConsumer extends DataConsumer implements KafkaMessageHandler {
    private final ExchangeEngine exchangeEngine;
    private final KafkaConsumerClient consumerClient;
    private final List<KafkaSubscriptionRequest> requests;

    public AuditTrailConsumer(ExchangeEngine exchangeEngine,
                              KafkaConsumerClient consumerClient,
                              List<KafkaSubscriptionDetails> subscriptionDetails) {
        super("Audit Trail", new CountDownLatch(subscriptionDetails.size()));
        this.exchangeEngine = exchangeEngine;
        this.consumerClient = consumerClient;
        this.requests = subscriptionDetails.stream().map(d -> new KafkaSubscriptionRequest(d, this)).toList();
    }

    @Override
    public void consumerInit() {
        requests.forEach(consumerClient::subscribeToBroadcastTopic);
    }

    @Override
    public void onMessage(BroadcastMessage broadcastMessage) {
        Message message = broadcastMessage.message();
        if (message instanceof OrderbookEvent orderbookEvent) {
            exchangeEngine.handleOrderbookEvents(orderbookEvent);

        } else if (message instanceof DataStreamState state) {
            switch (state.state()) {
                case START -> logger.info("Started consuming audit trail.");
                case DONE -> {
                    consumerClient.stop(broadcastMessage.partitionKey());
                    countDownLatch.countDown();
                    if (countDownLatch.getCount() == 0) {
                        consumerComplete();
                    }
                }
            }
        }
    }
}
