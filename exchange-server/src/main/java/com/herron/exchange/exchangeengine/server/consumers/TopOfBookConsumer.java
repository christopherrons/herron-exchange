package com.herron.exchange.exchangeengine.server.consumers;

import com.herron.exchange.common.api.common.api.Message;
import com.herron.exchange.common.api.common.api.kafka.KafkaMessageHandler;
import com.herron.exchange.common.api.common.consumer.DataConsumer;
import com.herron.exchange.common.api.common.kafka.KafkaConsumerClient;
import com.herron.exchange.common.api.common.kafka.model.KafkaSubscriptionDetails;
import com.herron.exchange.common.api.common.kafka.model.KafkaSubscriptionRequest;
import com.herron.exchange.common.api.common.messages.BroadcastMessage;
import com.herron.exchange.common.api.common.messages.common.DataStreamState;
import com.herron.exchange.common.api.common.messages.trading.TopOfBook;
import com.herron.exchange.exchangeengine.server.ExchangeEngine;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class TopOfBookConsumer extends DataConsumer implements KafkaMessageHandler {
    private final ExchangeEngine exchangeEngine;
    private final KafkaConsumerClient consumerClient;
    private final List<KafkaSubscriptionRequest> requests;

    public TopOfBookConsumer(ExchangeEngine exchangeEngine,
                             KafkaConsumerClient consumerClient,
                             List<KafkaSubscriptionDetails> subscriptionDetails) {
        super("Top-of-Book", new CountDownLatch(subscriptionDetails.size()));
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
        if (message instanceof TopOfBook topOfBook) {
            exchangeEngine.handleOrderbookEvents(topOfBook);

        } else if (message instanceof DataStreamState state) {
            switch (state.state()) {
                case START -> logger.info("Started consuming top of book.");
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
