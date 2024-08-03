package com.herron.exchange.exchangeengine.server.rest;

import com.herron.exchange.common.api.common.api.referencedata.instruments.*;
import com.herron.exchange.common.api.common.api.referencedata.orderbook.OrderbookData;
import com.herron.exchange.common.api.common.messages.common.Timestamp;
import com.herron.exchange.common.api.common.messages.common.Tree;
import com.herron.exchange.common.api.common.messages.refdata.ImmutableInstrumentHierarchy;
import com.herron.exchange.common.api.common.messages.refdata.InstrumentHierarchy;

import java.util.*;

public class InstrumentHierarchyBuilder {
    private static final String ROOT = "herron-exchange";

    public static InstrumentHierarchy build(Collection<OrderbookData> orderbookData) {

        var rootNode = new Tree.TreeNode(ROOT, ROOT, new ArrayList<>());
        Map<String, Tree.TreeNode> idToChild = new HashMap<>();

        for (var hierarchy : getHierarchies(orderbookData)) {
            var parentNode = rootNode;
            for (int i = 1; i < hierarchy.size(); i++) {
                var name = hierarchy.get(i);
                var id = hierarchy.subList(0, i + 1).toString();

                if (!idToChild.containsKey(id)) {
                    var childNode = new Tree.TreeNode(id, name, new ArrayList<>());
                    idToChild.put(id, childNode);
                    parentNode.addChild(childNode);
                }
                parentNode = idToChild.get(id);
            }
        }

        return ImmutableInstrumentHierarchy.builder()
                .timeStamp(Timestamp.now())
                .instrumentTree(new Tree(rootNode))
                .build();
    }

    private static List<List<String>> getHierarchies(Collection<OrderbookData> orderbookData) {
        List<List<String>> hierarchies = new ArrayList<>();

        for (var orderBookData : orderbookData) {
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
        return hierarchies;
    }

    private static void handleEquitiesHierarchy(List<String> hierarchy, EquityInstrument equityInstrument) {
        hierarchy.add("equities");
        hierarchy.add(equityInstrument.instrumentId());
    }

    private static void handleDerivativeHierarchy(List<String> hierarchy, DerivativeInstrument derivativeInstrument) {
        hierarchy.add("derivatives");
        switch (derivativeInstrument) {
            case FutureInstrument futureInstrument -> handleFuturesHierarchy(hierarchy, futureInstrument);
            case OptionInstrument optionInstrument -> handleOptionsHierarchy(hierarchy, optionInstrument);
            default -> hierarchy.add(derivativeInstrument.instrumentId());
        }
    }

    private static void handleFuturesHierarchy(List<String> hierarchy, FutureInstrument futureInstrument) {
        hierarchy.add("futures");
        var date = futureInstrument.maturityDate().toLocalDate();
        hierarchy.add(String.valueOf(date.getYear()));
        hierarchy.add(String.valueOf(date.getMonthValue()));
        hierarchy.add(String.valueOf(date.getDayOfMonth()));
        hierarchy.add(futureInstrument.instrumentId());
    }

    private static void handleOptionsHierarchy(List<String> hierarchy, OptionInstrument optionInstrument) {
        hierarchy.add("options");
        hierarchy.add(optionInstrument.optionSubType().getValue());
        var date = optionInstrument.maturityDate().toLocalDate();
        hierarchy.add(optionInstrument.optionExerciseStyle().getValue());
        hierarchy.add(optionInstrument.optionType().getValue());
        hierarchy.add(String.valueOf(date.getYear()));
        hierarchy.add(String.valueOf(date.getMonthValue()));
        hierarchy.add(String.valueOf(date.getDayOfMonth()));
        hierarchy.add("strike@" + optionInstrument.strikePrice().getValue().toString());
        hierarchy.add(optionInstrument.instrumentId());
    }

    private static void handleBondHierarchy(List<String> hierarchy, BondInstrument bondInstrument) {
        hierarchy.add("bonds");
        var date = bondInstrument.maturityDate().toLocalDate();
        hierarchy.add(String.valueOf(date.getYear()));
        hierarchy.add(String.valueOf(date.getMonthValue()));
        hierarchy.add(String.valueOf(date.getDayOfMonth()));
        hierarchy.add(bondInstrument.instrumentId());
    }
}
