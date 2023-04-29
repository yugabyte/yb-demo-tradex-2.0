package com.yugabyte.samples.tradex.api.utils;

public enum TradeXHistoryIntervalPeriod {

    ONE_HOUR("1H"),
    NINETY_MIN("90MIN"),
    ONE_DAY("1DAY"),
    ONE_WEEK("1WEEK"),
    THREE_MONTH("3MONTH");

    private final String value;

    TradeXHistoryIntervalPeriod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
