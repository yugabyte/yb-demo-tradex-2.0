package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;

public class TradeXDBTypeContext {

    private static final ThreadLocal<TradeXDataSourceType> CONTEXT = new ThreadLocal<>();

    public static void setDBType(TradeXDataSourceType type) {
        CONTEXT.set(type);
    }

    public static TradeXDataSourceType getDbType() {
        return null == CONTEXT.get() ? TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE : CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
