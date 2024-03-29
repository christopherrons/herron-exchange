package com.herron.exchange.exchange.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.api.trading.orders.Order;
import com.herron.exchange.common.api.common.api.trading.statechange.StateChange;
import com.herron.exchange.common.api.common.enums.MatchingAlgorithmEnum;
import com.herron.exchange.common.api.common.enum.StateChangeTypeEnum;
import com.herron.exchange.exchange.server.shadoworderbook.api.ShadowOrderbook;
import com.herron.exchange.exchange.server.shadoworderbook.comparator.ShadowOrderBookComparator;
import com.herron.exchange.exchange.server.shadoworderbook.model.OrderbookSnapshot;

import java.util.concurrent.atomic.AtomicLong;

public class ShadowOrderbookImpl implements ShadowOrderbook {

    private StateChangeTypeEnum stateChangeTypeEnum = StateChangeTypeEnum.INVALID_STATE_CHANGE;
    private final OrderbookData orderbookData;
    private final ActiveOrders activeOrders;
    private final AtomicLong currentSequenceNumber = new AtomicLong(1);

    private OrderbookSnapshot orderbookSnapshot;

    public ShadowOrderbookImpl(OrderbookData orderbookData) {
        this.orderbookData = orderbookData;
        this.activeOrders = new ActiveOrders(new ShadowOrderBookComparator());
    }

    public synchronized long updateOrderbook(Order order) {
        if (order.isActiveOrder()) {
            switch (order.orderOperation()) {
                case CREATE -> addOrder(order);
                case UPDATE -> updateOrder(order);
                case DELETE -> removeOrder(order);
            }
        }

        orderbookSnapshot = new OrderbookSnapshot(currentSequenceNumber.getAndIncrement(), activeOrders.getBidOrders(), activeOrders.getAskOrders());
        return orderbookSnapshot.sequenceNumber();
    }

    private void updateOrder(Order order) {
        activeOrders.updateOrder(order);
    }

    private void addOrder(Order order) {
        activeOrders.addOrder(order);
    }

    public void removeOrder(String orderId) {
        activeOrders.removeOrder(orderId);
    }

    private void removeOrder(Order order) {
        activeOrders.removeOrder(order);
    }

    @Override
    public double getBestBidPrice() {
        return activeOrders.getBestBidPrice();
    }

    @Override
    public double getBestAskPrice() {
        return activeOrders.getBestAskPrice();
    }

    @Override
    public boolean hasBidAndAskOrders() {
        return activeOrders.hasBidAndAskOrders();
    }

    @Override
    public long totalNumberOfBidOrders() {
        return activeOrders.totalNumberOfBidOrders();
    }

    @Override
    public long totalNumberOfAskOrders() {
        return activeOrders.totalNumberOfAskOrders();
    }

    @Override
    public long totalNumberOfActiveOrders() {
        return activeOrders.totalNumberOfActiveOrders();
    }

    @Override
    public double totalOrderVolume() {
        return activeOrders.totalOrderVolume();
    }

    @Override
    public double totalBidVolume() {
        return activeOrders.totalBidVolume();
    }

    @Override
    public double totalAskVolume() {
        return activeOrders.totalAskVolume();
    }

    @Override
    public double totalVolumeAtPriceLevel(int priceLevel) {
        return activeOrders.totalVolumeAtPriceLevel(priceLevel);
    }

    @Override
    public double totalBidVolumeAtPriceLevel(int priceLevel) {
        return activeOrders.totalBidVolumeAtPriceLevel(priceLevel);
    }

    @Override
    public double totalAskVolumeAtPriceLevel(int priceLevel) {
        return activeOrders.totalAskVolumeAtPriceLevel(priceLevel);
    }

    @Override
    public int totalNumberOfPriceLevels() {
        return activeOrders.totalNumberOfPriceLevels();
    }

    @Override
    public int totalNumberOfBidPriceLevels() {
        return activeOrders.totalNumberOfBidPriceLevels();
    }

    @Override
    public int totalNumberOfAskPriceLevels() {
        return activeOrders.totalNumberOfAskPriceLevels();
    }

    @Override
    public Order getOrder(String orderId) {
        return activeOrders.getOrder(orderId);
    }

    @Override
    public MatchingAlgorithmEnum getMatchingAlgorithm() {
        return orderbookData.matchingAlgorithm();
    }

    @Override
    public String getOrderbookId() {
        return orderbookData.orderbookId();
    }

    @Override
    public String getInstrumentId() {
        return orderbookData.instrumentId();
    }

    @Override
    public double getAskPriceAtPriceLevel(int priceLevel) {
        return activeOrders.getAskPriceAtPriceLevel(priceLevel);
    }

    @Override
    public double getBidPriceAtPriceLevel(int priceLevel) {
        return activeOrders.getBidPriceAtPriceLevel(priceLevel);
    }

    @Override
    public OrderbookSnapshot getOrderbookSnapshot() {
        return orderbookSnapshot;
    }

    @Override
    public long getCurrentSequenceNumber() {
        return currentSequenceNumber.get();
    }

    @Override
    public void updateState(StateChange stateChange) {
        stateChangeTypeEnum = stateChange.stateChangeType();
    }

    @Override
    public StateChangeTypeEnum getState() {
        return stateChangeTypeEnum;
    }
}
