package com.herron.exchange.exchange.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.trading.orders.Order;
import com.herron.exchange.common.api.common.enums.OrderSideEnum;
import com.herron.exchange.common.api.common.enums.OrderTypeEnum;
import com.herron.exchange.exchange.server.shadoworderbook.model.PriceLevel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ActiveOrders {
    private final Map<String, Order> orderIdToOrder = new ConcurrentHashMap<>();
    private final TreeMap<Double, PriceLevel> bidPriceToPriceLevel = new TreeMap<>(Comparator.reverseOrder());
    private final TreeMap<Double, PriceLevel> askPriceToPriceLevel = new TreeMap<>();
    private final Comparator<? super Order> comparator;

    public ActiveOrders(Comparator<? super Order> comparator) {
        this.comparator = comparator;
    }

    public void updateOrder(Order order) {
        removeOrder(order.orderId());
        addOrder(order);
    }

    public void addOrder(Order order) {
        PriceLevel priceLevel = findOrCreatePriceLevel(order);
        priceLevel.add(order);
        orderIdToOrder.putIfAbsent(order.orderId(), order);
    }

    public void removeOrder(Order order) {
        removeOrder(order.orderId());
    }

    public void removeOrder(String orderId) {
        if (!orderIdToOrder.containsKey(orderId)) {
            return;
        }
        Order order = orderIdToOrder.remove(orderId);
        PriceLevel priceLevel = findOrCreatePriceLevel(order);
        if (priceLevel != null) {
            priceLevel.remove(order);
            if (priceLevel.isEmpty()) {
                removePriceLevel(order);
            }
        }
    }

    private PriceLevel findOrCreatePriceLevel(Order order) {
        return switch (order.orderSide()) {
            case BID -> bidPriceToPriceLevel.computeIfAbsent(order.price(), key -> new PriceLevel(order.price(), comparator));
            case ASK -> askPriceToPriceLevel.computeIfAbsent(order.price(), key -> new PriceLevel(order.price(), comparator));
            case INVALID_ORDER_SIDE -> null;
        };
    }

    private void removePriceLevel(Order order) {
        switch (order.orderSide()) {
            case BID -> bidPriceToPriceLevel.remove(order.price());
            case ASK -> askPriceToPriceLevel.remove(order.price());
        }
    }

    public int totalNumberOfPriceLevels() {
        return totalNumberOfBidPriceLevels() + totalNumberOfAskPriceLevels();
    }

    public int totalNumberOfBidPriceLevels() {
        return bidPriceToPriceLevel.values().size();
    }

    public int totalNumberOfAskPriceLevels() {
        return askPriceToPriceLevel.values().size();
    }

    public Order getOrder(String orderId) {
        return orderIdToOrder.get(orderId);
    }

    public double getBestBidPrice() {
        return getBestBidOrder().map(Order::price).orElse(0.0);
    }

    public double getBestAskPrice() {
        return getBestAskOrder().map(Order::price).orElse(0.0);
    }

    public Optional<Order> getBestBidOrder() {
        return getBestOrder(OrderSideEnum.BID);
    }

    public Optional<Order> getBestAskOrder() {
        return getBestOrder(OrderSideEnum.ASK);
    }

    private Optional<Order> getBestOrder(OrderSideEnum orderSide) {
        return switch (orderSide) {
            case BID -> bidPriceToPriceLevel.values().stream().findFirst().map(PriceLevel::first);
            case ASK -> askPriceToPriceLevel.values().stream().findFirst().map(PriceLevel::first);
            case INVALID_ORDER_SIDE -> Optional.empty();
        };
    }

    public long totalNumberOfBidOrders() {
        return totalNumberOfSideOrders(OrderSideEnum.BID.getValue());
    }

    public long totalNumberOfAskOrders() {
        return totalNumberOfSideOrders(OrderSideEnum.ASK.getValue());
    }

    private long totalNumberOfSideOrders(int orderSide) {
        return switch (OrderSideEnum.fromValue(orderSide)) {
            case BID -> bidPriceToPriceLevel.values().stream().mapToLong(PriceLevel::nrOfOrdersAtPriceLevel).sum();
            case ASK -> askPriceToPriceLevel.values().stream().mapToLong(PriceLevel::nrOfOrdersAtPriceLevel).sum();
            case INVALID_ORDER_SIDE -> 0;
        };
    }

    public long totalNumberOfActiveOrders() {
        return orderIdToOrder.size();
    }

    public double totalOrderVolume() {
        return totalBidVolume() + totalAskVolume();
    }

    public double totalBidVolume() {
        return bidPriceToPriceLevel.values().stream().mapToDouble(PriceLevel::volumeAtPriceLevel).sum();
    }

    public double totalAskVolume() {
        return askPriceToPriceLevel.values().stream().mapToDouble(PriceLevel::volumeAtPriceLevel).sum();
    }

    public double totalVolumeAtPriceLevel(int priceLevel) {
        return totalBidVolumeAtPriceLevel(priceLevel) + totalAskVolumeAtPriceLevel(priceLevel);
    }

    public double totalBidVolumeAtPriceLevel(int priceLevel) {
        return bidPriceToPriceLevel.values().stream()
                .skip(priceLevel - 1L)
                .findFirst().map(PriceLevel::volumeAtPriceLevel)
                .orElse(0.0);
    }

    public double totalAskVolumeAtPriceLevel(int priceLevel) {
        return askPriceToPriceLevel.values().stream()
                .skip(priceLevel - 1L)
                .findFirst().map(PriceLevel::volumeAtPriceLevel)
                .orElse(0.0);
    }

    public double getAskPriceAtPriceLevel(int priceLevel) {
        return askPriceToPriceLevel.values().stream()
                .skip(priceLevel - 1L)
                .findFirst().map(PriceLevel::getPrice)
                .orElse(0.0);
    }

    public double getBidPriceAtPriceLevel(int priceLevel) {
        return bidPriceToPriceLevel.values().stream()
                .skip(priceLevel - 1L)
                .findFirst().map(PriceLevel::getPrice)
                .orElse(0.0);
    }

    public boolean hasBidAndAskOrders() {
        return bidPriceToPriceLevel.size() != 0 && askPriceToPriceLevel.size() != 0;
    }

    public boolean isTotalFillPossible(Order order) {
        return switch (order.orderSide()) {
            case BID -> isTotalBidFillPossible(order);
            case ASK -> isTotalAskFillPossible(order);
            default -> false;
        };
    }

    public Optional<PriceLevel> getBestBidPriceLevel() {
        return getBestPriceLevel(OrderSideEnum.ASK);
    }

    public Optional<PriceLevel> getBestAskPriceLevel() {
        return getBestPriceLevel(OrderSideEnum.ASK);
    }

    private Optional<PriceLevel> getBestPriceLevel(OrderSideEnum orderSide) {
        return switch (orderSide) {
            case BID -> bidPriceToPriceLevel.values().stream().findFirst();
            case ASK -> askPriceToPriceLevel.values().stream().findFirst();
            case INVALID_ORDER_SIDE -> Optional.empty();
        };
    }

    private boolean isTotalAskFillPossible(Order order) {
        double availableVolume = 0;
        for (var level : bidPriceToPriceLevel.values()) {
            if (order.orderType() == OrderTypeEnum.MARKET || order.price() <= level.getPrice()) {
                availableVolume = level.volumeAtPriceLevel();
            } else {
                return false;
            }

            if (order.currentVolume() <= availableVolume) {
                return true;
            }
        }
        return false;
    }

    private boolean isTotalBidFillPossible(Order order) {
        double availableVolume = 0;
        for (var level : askPriceToPriceLevel.values()) {
            if (order.price() >= level.getPrice()) {
                availableVolume += level.volumeAtPriceLevel();
            } else {
                return false;
            }

            if (order.currentVolume() <= availableVolume) {
                return true;
            }
        }
        return false;
    }

    public List<Order> getBidOrders() {
        return bidPriceToPriceLevel.values().stream().flatMap(PriceLevel::getOrderStream).toList();
    }

    public List<Order> getAskOrders() {
        return askPriceToPriceLevel.values().stream().flatMap(PriceLevel::getOrderStream).toList();
    }

}
