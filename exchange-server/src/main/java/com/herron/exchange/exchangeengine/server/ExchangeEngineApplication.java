package com.herron.exchange.exchangeengine.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class ExchangeEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeEngineApplication.class, args);
    }

}
