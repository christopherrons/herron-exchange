package com.herron.exchange.exchange.server.rest.orderbook;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@CrossOrigin("*")
@RequestMapping("/orderbook")
public class OrderbookRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderbookRestController.class);

    @Autowired
    private OrderbookRestService orderbookRestService;

    @GetMapping("/snaphot")
    @Operation(summary = "Request orderbook snapshot.",
            description = "This method returns the latest orderbook snapshot.")
    public OrderbookSnapshotDto getOrderbookSnapshot(OrderbookSnapshotRestRequest orderbookSnapshotRestRequest) {
        LOGGER.info("Get request for latest orderbook snapshot received.");
        return orderbookRestService.getOrderbookSnapshot(orderbookSnapshotRestRequest);
    }


}
