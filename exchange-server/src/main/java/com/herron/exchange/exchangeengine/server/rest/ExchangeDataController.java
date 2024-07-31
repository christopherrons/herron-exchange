package com.herron.exchange.exchangeengine.server.rest;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.exchangeengine.server.ExchangeEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@CrossOrigin("*")
@RequestMapping("/exchangeData")
public class ExchangeDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeDataController.class);
    private final ExchangeDataService exchangeDataService;

    public ExchangeDataController(ExchangeDataService exchangeDataService) {
        this.exchangeDataService = exchangeDataService;
    }

    @GetMapping("/availableOrderbookIds")
    public Set<String> getOrderbookIds() {
        LOGGER.info("Get request available orderbooks received.");
        return exchangeDataService.getOrderbookIds();
    }

    @GetMapping("/orderbookState")
    public TradingStatesEnum getOrderbookState(String orderbookId) {
        LOGGER.info("Get request for {} state received.", orderbookId);
        return exchangeDataService.getOrderbookState(orderbookId);
    }
}
