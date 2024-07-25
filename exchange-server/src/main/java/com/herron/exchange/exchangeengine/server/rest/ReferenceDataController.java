package com.herron.exchange.exchangeengine.server.rest;

import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
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
@RequestMapping("/referenceData")
public class ReferenceDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataController.class);


    @GetMapping("/availableOrderbookIds")
    public Set<String> getOrderbookIds() {
        LOGGER.info("Get request available instruments received.");
        return ReferenceDataCache.getCache().getOrderbookData().stream().map(OrderbookData::orderbookId).collect(Collectors.toSet());
    }
}
