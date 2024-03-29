package com.herron.exchange.exchange.server.shadoworderbook.model;


import com.herron.exchange.common.api.common.api.trading.orders.Order;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PriceLevel extends TreeSet<Order> {

    private final double price;

    public PriceLevel(double price, Comparator<? super Order> comparator) {
        super(comparator);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public long nrOfOrdersAtPriceLevel() {
        return size();
    }

    public double volumeAtPriceLevel() {
        return stream().mapToDouble(Order::currentVolume).sum();
    }

    public double volumeAtPriceLevel(Predicate<Order> filter) {
        return stream()
                .filter(filter)
                .mapToDouble(Order::currentVolume)
                .sum();
    }

    public Stream<Order> getOrderStream() {
        return stream();
    }

}
