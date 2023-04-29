package com.yugabyte.samples.tradex.api.utils;

public class AppConstants {
    public enum Caches {
        TRAFFIC_LOCATIONS,
        DB_CLUSTER_TYPES,
        STOCK_SYMBOLS,
        STOCK_SYMBOLS_SINGLE,
        DB_NODES,
        DB_NODES_LOCATIONS,
        SINGLE_STOCK,
        ALL_STOCKS,
        SINGLE_STOCK_HIST,
        API_NODE_LOCATIONS,
        DEFAULT_NODE_LOCATIONS
    }

    public enum PayMethod {
        DEBIT_CARD,
        CREDIT_CARD,
        BANK_TRANSFER
    }

    public enum TradeType {
        BUY,
        SELL
    }
}
