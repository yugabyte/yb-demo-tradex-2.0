package com.yugabyte.samples.tradex.api.config;

public enum TradeXDataSourceType {
    SINGLE_REGION_MULTI_ZONE,
    MULTI_REGION_MULTI_ZONE,
    MULTI_REGION_READ_REPLICA,
    GEO_PARTITIONED
}
