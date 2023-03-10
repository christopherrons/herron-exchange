package com.herron.exchange.exchange.server.rest.errorhandling.model;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ApiException(String message,
                           Throwable throwable,
                           HttpStatus httpStatus,
                           ZonedDateTime timeStamp) {
}
