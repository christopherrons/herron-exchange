package com.herron.exchange.exchangeengine.server.rest;

import com.herron.exchange.common.api.common.enums.TradingStatesEnum;
import com.herron.exchange.common.api.common.messages.refdata.InstrumentHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchangeData")
public class ExchangeDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeDataController.class);
    private final ExchangeDataService exchangeDataService;

    public ExchangeDataController(ExchangeDataService exchangeDataService) {
        this.exchangeDataService = exchangeDataService;
    }

    @GetMapping("/orderbookState")
    public TradingStatesEnum getOrderbookState(@RequestParam("orderbookId") String orderbookId) {
        LOGGER.info("Get request for {} state received.", orderbookId);
        return exchangeDataService.getOrderbookState(orderbookId);
    }

    @GetMapping("/instrumentHierarchy")
    public InstrumentHierarchy getInstrumentHierarchy() {
        LOGGER.info("Get request instrument hierarchy received");
        return exchangeDataService.getInstrumentHierarchy();
    }
}