package com.herron.exchange.exchangeengine.server;

import com.herron.exchange.common.api.common.api.referencedata.instruments.*;
import com.herron.exchange.common.api.common.cache.ReferenceDataCache;
import com.herron.exchange.common.api.common.messages.refdata.InstrumentHierarchyGroup;

import java.util.*;

public class InstrumentHierarchyBuilder {
    private static final String ROOT = "herron-exchange";

    public InstrumentHierarchyGroup build() {
        List<List<String>> hierarchies = new ArrayList<>();
        for (var orderBookData : ReferenceDataCache.getCache().getOrderbookData()) {
            var hierarchy = new ArrayList<String>();
            hierarchy.add(ROOT);
            var instrument = orderBookData.instrument();
            switch (instrument) {
                case EquityInstrument equityInstrument -> handleEquitiesHierarchy(hierarchy, equityInstrument);
                case DerivativeInstrument derivativeInstrument -> handleDerivativeHierarchy(hierarchy, derivativeInstrument);
                case BondInstrument bondInstrument -> handleBondHierarchy(hierarchy, bondInstrument);
                default -> hierarchy.add(ROOT);
            }
            hierarchies.add(hierarchy);
        }

        Map<String, Set<String>> parentIdToChildId = new HashMap<>();
        Map<String, Set<String>> childIdToParentId = new HashMap<>();
        Map<String, Set<String>> groupIdToInstrumentId = new HashMap<>();
        for (var hierarchy : hierarchies) {
            var parent = ROOT;
            for (int i = 1; i < hierarchy.size(); i++) {
                var child = hierarchy.get(i);
                if (i == hierarchy.size() - 1) {
                    groupIdToInstrumentId.computeIfAbsent(parent, k -> new HashSet<>()).add(child);
                } else {
                    parentIdToChildId.computeIfAbsent(parent, k -> new HashSet<>()).add(child);
                    childIdToParentId.computeIfAbsent(child, k -> new HashSet<>()).add(parent);
                    parent = child;
                }
            }
        }
        return null;
    }

    private void handleEquitiesHierarchy(List<String> hierarchy, EquityInstrument equityInstrument) {
        hierarchy.add("equities");
        hierarchy.add(equityInstrument.instrumentId());
    }

    private void handleDerivativeHierarchy(List<String> hierarchy, DerivativeInstrument derivativeInstrument) {
        hierarchy.add("derivatives");
        switch (derivativeInstrument) {
            case FutureInstrument futureInstrument -> handleFuturesHierarchy(hierarchy, futureInstrument);
            case OptionInstrument optionInstrument -> handleOptionsHierarchy(hierarchy, optionInstrument);
            default -> hierarchy.add(derivativeInstrument.instrumentId());
        }
    }

    private void handleFuturesHierarchy(List<String> hierarchy, FutureInstrument futureInstrument) {
        hierarchy.add("futures");
        var date = futureInstrument.maturityDate().toLocalDate();
        hierarchy.add(String.valueOf(date.getYear()));
        hierarchy.add(String.valueOf(date.getMonthValue()));
        hierarchy.add(String.valueOf(date.getDayOfMonth()));
        hierarchy.add(futureInstrument.instrumentId());
    }

    private void handleOptionsHierarchy(List<String> hierarchy, OptionInstrument optionInstrument) {
        hierarchy.add("options");
        var date = optionInstrument.maturityDate().toLocalDate();
        hierarchy.add(String.valueOf(date.getYear()));
        hierarchy.add(String.valueOf(date.getMonthValue()));
        hierarchy.add(String.valueOf(date.getDayOfMonth()));
        hierarchy.add(optionInstrument.strikePrice().getValue().toString());
        hierarchy.add(optionInstrument.optionSubType().getValue());
        hierarchy.add(optionInstrument.optionExerciseStyle().getValue());
        hierarchy.add(optionInstrument.optionType().getValue());
        hierarchy.add(optionInstrument.instrumentId());
    }

    private void handleBondHierarchy(List<String> hierarchy, BondInstrument bondInstrument) {
        hierarchy.add("bonds");
        var date = bondInstrument.maturityDate().toLocalDate();
        hierarchy.add(String.valueOf(date.getYear()));
        hierarchy.add(String.valueOf(date.getMonthValue()));
        hierarchy.add(String.valueOf(date.getDayOfMonth()));
        hierarchy.add(bondInstrument.instrumentId());
    }
}
