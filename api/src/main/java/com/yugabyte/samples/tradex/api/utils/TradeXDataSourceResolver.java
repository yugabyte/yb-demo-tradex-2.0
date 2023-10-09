package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TradeXDataSourceResolver {

    @Autowired
    @Qualifier("SINGLE_DB")
    DataSource singleDbDataSource;

    @Autowired(required = false)
    @Qualifier("MULTI_REGION_DB")
    DataSource multiRegionDB;

    @Autowired(required = false)
    @Qualifier("MULTI_REGION_READ_REPLICA_DB")
    DataSource multiNodeReadReplicaDB;

    @Autowired(required = false)
    @Qualifier("GEO_PARTITIONED_DB")
    DataSource geoPartitionedDB;


    public DataSource resolve(TradeXDataSourceType dbType) {
        return switch (dbType) {
            case SINGLE_REGION_MULTI_ZONE -> singleDbDataSource;
            case MULTI_REGION_MULTI_ZONE -> multiRegionDB;
            case MULTI_REGION_READ_REPLICA -> multiNodeReadReplicaDB;
            case GEO_PARTITIONED -> geoPartitionedDB;
            default -> throw new IllegalStateException("Unexpected value: " + dbType);
        };
    }
}
