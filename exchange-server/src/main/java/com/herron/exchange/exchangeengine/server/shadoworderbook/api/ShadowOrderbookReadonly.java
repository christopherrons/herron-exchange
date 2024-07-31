package com.herron.exchange.exchangeengine.server.shadoworderbook.api;

import com.herron.exchange.common.api.common.enums.TradingStatesEnum;

public interface ShadowOrderbookReadonly {

    TradingStatesEnum getState();
}
