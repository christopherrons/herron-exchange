package com.herron.exchange.exchange.server.shadoworderbook.api;

import com.herron.exchange.common.api.common.api.trading.orders.Order;
import com.herron.exchange.common.api.common.api.trading.statechange.StateChange;
import com.herron.exchange.common.api.common.enums.MatchingAlgorithmEnum;
import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.exchange.server.shadoworderbook.model.OrderbookSnapshot;

public interface ShadowOrderbook {

    long updateOrderbook(Order order);

    void updateState(StateChange stateChange);

    TradingStatesEnum getState();

    String getOrderbookId();

    MatchingAlgorithmEnum getMatchingAlgorithm();

    boolean hasBidAndAskOrders();

    Order getOrder(String orderId);

    double totalOrderVolume();

    double totalBidVolume();

    double totalAskVolume();

    double getBestBidPrice();

    double getBestAskPrice();

    long totalNumberOfBidOrders();

    long totalNumberOfAskOrders();

    long totalNumberOfActiveOrders();

    double totalVolumeAtPriceLevel(int priceLevel);

    double totalBidVolumeAtPriceLevel(int priceLevel);

    double totalAskVolumeAtPriceLevel(int priceLevel);

    int totalNumberOfPriceLevels();

    int totalNumberOfBidPriceLevels();

    int totalNumberOfAskPriceLevels();

    String getInstrumentId();

    double getAskPriceAtPriceLevel(int priceLevel);

    double getBidPriceAtPriceLevel(int priceLevel);

    OrderbookSnapshot getOrderbookSnapshot();

    long getCurrentSequenceNumber();
}
