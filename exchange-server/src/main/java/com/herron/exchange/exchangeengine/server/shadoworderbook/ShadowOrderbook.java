package com.herron.exchange.exchangeengine.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.api.trading.Order;
import com.herron.exchange.common.api.common.enums.MatchingAlgorithmEnum;
import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.common.api.common.messages.common.Price;
import com.herron.exchange.common.api.common.messages.common.Timestamp;
import com.herron.exchange.common.api.common.messages.common.Volume;
import com.herron.exchange.common.api.common.messages.trading.ImmutablePriceQuote;
import com.herron.exchange.common.api.common.messages.trading.ImmutableTopOfBook;
import com.herron.exchange.common.api.common.messages.trading.PriceQuote;
import com.herron.exchange.common.api.common.messages.trading.TopOfBook;
import com.herron.exchange.exchangeengine.server.shadoworderbook.api.ShadowOrderbookReadonly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.herron.exchange.common.api.common.enums.EventType.SYSTEM;
import static com.herron.exchange.common.api.common.enums.QuoteTypeEnum.ASK_PRICE;
import static com.herron.exchange.common.api.common.enums.QuoteTypeEnum.BID_PRICE;
import static com.herron.exchange.common.api.common.enums.TradingStatesEnum.CLOSED;
import static com.herron.exchange.common.api.common.enums.TradingStatesEnum.TRADE_HALT;


public class ShadowOrderbook implements ShadowOrderbookReadonly {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShadowOrderbook.class);
    private final OrderbookData orderbookData;
    private final ActiveOrders activeOrders;
    private TradingStatesEnum currentState = CLOSED;
    private final AtomicReference<PriceQuote> latestPrice = new AtomicReference<>();

    public ShadowOrderbook(OrderbookData orderbookData, ActiveOrders activeOrders) {
        this.orderbookData = orderbookData;
        this.activeOrders = activeOrders;
    }

    public synchronized boolean updateOrderbook(Order order) {
        if (!isAccepting()) {
            LOGGER.error("Is not accepting {}.", currentState);
            return false;
        }
        if (order.isActiveOrder()) {
            return switch (order.orderOperation()) {
                case INSERT -> addOrder(order);
                case UPDATE -> updateOrder(order);
                case CANCEL -> removeOrder(order);
            };
        }
        return true;
    }

    public boolean isAccepting() {
        //FIXME: Add acceptable operations based on the current state.
        if (currentState == null) {
            return false;
        }
        if (currentState == TRADE_HALT) {
            return false;
        }

        if (currentState == CLOSED) {
            return false;
        }
        return true;
    }

    private boolean updateOrder(Order order) {
        return activeOrders.updateOrder(order);
    }

    private boolean addOrder(Order order) {
        return activeOrders.addOrder(order);
    }

    public boolean removeOrder(String orderId) {
        return activeOrders.removeOrder(orderId);
    }

    private boolean removeOrder(Order order) {
        return activeOrders.removeOrder(order);
    }

    public Optional<Price> getBestBidPrice() {
        return activeOrders.getBestBidPrice();
    }

    public Optional<Price> getBestAskPrice() {
        return activeOrders.getBestAskPrice();
    }

    public boolean hasBidAndAskOrders() {
        return activeOrders.hasBidAndAskOrders();
    }

    public long totalNumberOfBidOrders() {
        return activeOrders.totalNumberOfBidOrders();
    }

    public long totalNumberOfAskOrders() {
        return activeOrders.totalNumberOfAskOrders();
    }

    public long totalNumberOfActiveOrders() {
        return activeOrders.totalNumberOfActiveOrders();
    }

    public Volume totalOrderVolume() {
        return activeOrders.totalOrderVolume();
    }

    public Volume totalBidVolume() {
        return activeOrders.totalBidVolume();
    }

    public Volume totalAskVolume() {
        return activeOrders.totalAskVolume();
    }

    public Volume totalVolumeAtPriceLevel(int priceLevel) {
        return activeOrders.totalVolumeAtPriceLevel(priceLevel);
    }

    public Volume totalBidVolumeAtPriceLevel(int priceLevel) {
        return activeOrders.totalBidVolumeAtPriceLevel(priceLevel);
    }

    public Volume totalAskVolumeAtPriceLevel(int priceLevel) {
        return activeOrders.totalAskVolumeAtPriceLevel(priceLevel);
    }

    public int totalNumberOfPriceLevels() {
        return activeOrders.totalNumberOfPriceLevels();
    }

    public int totalNumberOfBidPriceLevels() {
        return activeOrders.totalNumberOfBidPriceLevels();
    }

    public int totalNumberOfAskPriceLevels() {
        return activeOrders.totalNumberOfAskPriceLevels();
    }

    public Order getOrder(String orderId) {
        return activeOrders.getOrder(orderId);
    }

    public MatchingAlgorithmEnum getMatchingAlgorithm() {
        return orderbookData.matchingAlgorithm();
    }

    public String getOrderbookId() {
        return orderbookData.orderbookId();
    }

    public String getInstrumentId() {
        return orderbookData.instrument().instrumentId();
    }

    public Price getAskPriceAtPriceLevel(int priceLevel) {
        return activeOrders.getAskPriceAtPriceLevel(priceLevel);
    }

    public Price getBidPriceAtPriceLevel(int priceLevel) {
        return activeOrders.getBidPriceAtPriceLevel(priceLevel);
    }

    public Optional<Order> getBestBidOrder() {
        return activeOrders.getBestBidOrder();
    }

    public TopOfBook getTopOfBook() {
        var builder = ImmutableTopOfBook.builder()
                .orderbookId(getOrderbookId())
                .timeOfEvent(Timestamp.now())
                .eventType(SYSTEM);

        Optional.ofNullable(latestPrice.get()).ifPresent(builder::lastQuote);
        getBestAskOrder()
                .map(ao -> ImmutablePriceQuote.builder().orderbookId(getOrderbookId()).price(ao.price()).eventType(ao.eventType()).timeOfEvent(ao.timeOfEvent()).quoteType(ASK_PRICE).build())
                .ifPresent(builder::askQuote);
        getBestBidOrder()
                .map(bo -> ImmutablePriceQuote.builder().orderbookId(getOrderbookId()).price(bo.price()).eventType(bo.eventType()).timeOfEvent(bo.timeOfEvent()).quoteType(BID_PRICE).build())
                .ifPresent(builder::bidQuote);

        return builder.build();
    }

    public Optional<Order> getBestAskOrder() {
        return activeOrders.getBestAskOrder();
    }

    public boolean updateState(TradingStatesEnum toState) {
        if (toState == currentState) {
            return true;
        }

        if (currentState == null || currentState.isValidStateChange(toState)) {
            LOGGER.info("Successfully updated orderbook {} from state {} to state {}.", getOrderbookId(), currentState, toState);
            currentState = toState;
            return true;
        }
        LOGGER.error("Could not updated orderbook {} from state {} to state {}.", getOrderbookId(), currentState, toState);
        return false;
    }

    public TradingStatesEnum getState() {
        return currentState;
    }
}
