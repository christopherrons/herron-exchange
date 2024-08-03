package com.herron.exchange.exchangeengine.server;

import com.herron.exchange.common.api.common.bootloader.Bootloader;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.exchangeengine.server.consumers.AuditTrailConsumer;
import com.herron.exchange.exchangeengine.server.consumers.ReferenceDataConsumer;
import com.herron.exchange.exchangeengine.server.consumers.TopOfBookConsumer;
import com.herron.exchange.exchangeengine.server.rest.InstrumentHierarchyBuilder;

public class ExchangeEngineBootloader extends Bootloader {
    private final ReferenceDataConsumer referenceDataConsumer;
    private final TopOfBookConsumer topOfBookConsumer;
    private final AuditTrailConsumer auditTrailConsumer;

    public ExchangeEngineBootloader(ReferenceDataConsumer referenceDataConsumer,
                                    TopOfBookConsumer topOfBookConsumer,
                                    AuditTrailConsumer auditTrailConsumer) {
        super("Exchange Engine");
        this.referenceDataConsumer = referenceDataConsumer;
        this.topOfBookConsumer = topOfBookConsumer;
        this.auditTrailConsumer = auditTrailConsumer;
    }

    @Override
    protected void bootloaderInit() {
        referenceDataConsumer.init();
        referenceDataConsumer.await();
        topOfBookConsumer.init();
        auditTrailConsumer.init();
        bootloaderComplete();
    }
}
