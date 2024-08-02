package com.herron.exchange.exchangeengine.server.shadoworderbook;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.api.trading.Order;
import com.herron.exchange.common.api.common.enums.MatchingAlgorithmEnum;
import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.common.api.common.locks.LockHandler;
import com.herron.exchange.common.api.common.messages.common.Price;
import com.herron.exchange.common.api.common.messages.common.Timestamp;
import com.herron.exchange.common.api.common.messages.common.Volume;
import com.herron.exchange.common.api.common.messages.trading.*;
import com.herron.exchange.exchangeengine.server.shadoworderbook.api.ShadowOrderbookReadonly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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
    private final LockHandler lock = new LockHandler();

    public ShadowOrderbook(OrderbookData orderbookData, ActiveOrders activeOrders) {
        this.orderbookData = orderbookData;
        this.activeOrders = activeOrders;
    }

    public synchronized boolean updateOrderbook(Order order) {
        return lock.executeWithWriteLock(() -> {
                    if (order.isActiveOrder()) {
                        return switch (order.orderOperation()) {
                            case INSERT -> addOrder(order);
                            case UPDATE -> updateOrder(order);
                            case CANCEL -> removeOrder(order);
                        };
                    }
                    return true;
                }
        );
    }

    public boolean isAccepting() {
        return lock.executeWithReadLock(() -> {
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
        );
    }

    private boolean updateOrder(Order order) {
        return lock.executeWithWriteLock(() -> activeOrders.updateOrder(order));
    }

    private boolean addOrder(Order order) {
        return lock.executeWithWriteLock(() -> activeOrders.addOrder(order));
    }

    public boolean removeOrder(String orderId) {
        return lock.executeWithWriteLock(() -> activeOrders.removeOrder(orderId));
    }

    private boolean removeOrder(Order order) {
        return lock.executeWithWriteLock(() -> activeOrders.removeOrder(order));
    }

    public Optional<Price> getBestBidPrice() {
        return lock.executeWithReadLock(activeOrders::getBestBidPrice);
    }

    public Optional<Price> getBestAskPrice() {
        return lock.executeWithReadLock(activeOrders::getBestAskPrice);
    }

    public boolean hasBidAndAskOrders() {
        return lock.executeWithReadLock(activeOrders::hasBidAndAskOrders);
    }

    public long totalNumberOfBidOrders() {
        return lock.executeWithReadLock(activeOrders::totalNumberOfBidOrders);
    }

    public long totalNumberOfAskOrders() {
        return lock.executeWithReadLock(activeOrders::totalNumberOfAskOrders);
    }

    public long totalNumberOfActiveOrders() {
        return lock.executeWithReadLock(activeOrders::totalNumberOfActiveOrders);
    }

    public Volume totalOrderVolume() {
        return lock.executeWithReadLock(activeOrders::totalOrderVolume);
    }

    public Volume totalBidVolume() {
        return lock.executeWithReadLock(activeOrders::totalBidVolume);
    }

    public Volume totalAskVolume() {
        return lock.executeWithReadLock(activeOrders::totalAskVolume);
    }

    public Volume totalVolumeAtPriceLevel(int priceLevel) {
        return lock.executeWithReadLock(() -> activeOrders.totalVolumeAtPriceLevel(priceLevel));
    }

    public Volume totalBidVolumeAtPriceLevel(int priceLevel) {
        return lock.executeWithReadLock(() -> activeOrders.totalBidVolumeAtPriceLevel(priceLevel));
    }

    public Volume totalAskVolumeAtPriceLevel(int priceLevel) {
        return lock.executeWithReadLock(() -> activeOrders.totalAskVolumeAtPriceLevel(priceLevel));
    }

    public int totalNumberOfPriceLevels() {
        return lock.executeWithReadLock(activeOrders::totalNumberOfPriceLevels);
    }

    public int totalNumberOfBidPriceLevels() {
        return lock.executeWithReadLock(activeOrders::totalNumberOfBidPriceLevels);
    }

    public int totalNumberOfAskPriceLevels() {
        return lock.executeWithReadLock(activeOrders::totalNumberOfAskPriceLevels);
    }

    public Order getOrder(String orderId) {
        return lock.executeWithReadLock(() -> activeOrders.getOrder(orderId));
    }

    public MatchingAlgorithmEnum getMatchingAlgorithm() {
        return lock.executeWithReadLock(orderbookData::matchingAlgorithm);
    }

    public String getOrderbookId() {
        return lock.executeWithReadLock(orderbookData::orderbookId);
    }

    public String getInstrumentId() {
        return lock.executeWithReadLock(() -> orderbookData.instrument().instrumentId());
    }

    public Optional<Price> getAskPriceAtPriceLevel(int priceLevel) {
        return lock.executeWithReadLock(() -> activeOrders.getAskPriceAtPriceLevel(priceLevel));
    }

    public Optional<Price> getBidPriceAtPriceLevel(int priceLevel) {
        return lock.executeWithReadLock(() -> activeOrders.getBidPriceAtPriceLevel(priceLevel));
    }

    public Optional<Order> getBestBidOrder() {
        return lock.executeWithReadLock(activeOrders::getBestBidOrder);
    }

    public TopOfBook getTopOfBook() {
        return lock.executeWithReadLock(() -> {
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
        );
    }

    public MarketByLevel getMarketByLevel(int nrOfLevels) {
        return lock.executeWithReadLock(() -> {
                    List<MarketByLevel.LevelData> levelData = new ArrayList<>();
                    for (int level = 1; level < nrOfLevels + 1; level++) {
                        if (!activeOrders.doOrdersExistAtLevel(level)) {
                            break;
                        }

                        var builder = ImmutableLevelData.builder().level(level);

                        if (activeOrders.doesBidLevelExist(level)) {
                            activeOrders.getBidPriceAtPriceLevel(level).ifPresent(builder::bidPrice);
                            builder.bidVolume(activeOrders.totalBidVolumeAtPriceLevel(level));
                            builder.nrOfBidOrders(activeOrders.totalNrOfBidOrdersAtPriceLevel(level));
                        }

                        if (activeOrders.doesAskLevelExist(level)) {
                            activeOrders.getAskPriceAtPriceLevel(level).ifPresent(builder::askPrice);
                            builder.askVolume(activeOrders.totalAskVolumeAtPriceLevel(level));
                            builder.nrOfAskOrders(activeOrders.totalNrOfAskOrdersAtPriceLevel(level));
                        }

                        levelData.add(builder.build());

                    }
                    return ImmutableMarketByLevel.builder()
                            .orderbookId(getOrderbookId())
                            .timeOfEvent(Timestamp.now())
                            .levelData(levelData)
                            .eventType(SYSTEM)
                            .build();
                }
        );
    }

    public Optional<Order> getBestAskOrder() {
        return lock.executeWithReadLock(activeOrders::getBestAskOrder);
    }

    public boolean updateState(TradingStatesEnum toState) {
        return lock.executeWithWriteLock(() -> {
                    currentState = toState;
                    return true;
                }
        );
    }

    public TradingStatesEnum getState() {
        return lock.executeWithReadLock(() -> currentState);
    }
}