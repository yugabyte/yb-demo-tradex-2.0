package com.yugabyte.samples.tradex.api.utils;

public class ExecutedTradeOrderDetails {

    public long dbTime;
    public Integer order_id;

    public ExecutedTradeOrderDetails(long dbTime, Integer order_id) {
        this.dbTime = dbTime;
        this.order_id = order_id;
    }
}
