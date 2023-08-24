package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeXJdbcTemplateResolver {

    @Autowired
    @Qualifier("SINGLE_DB_TEMPLATE")
    NamedParameterJdbcTemplate singleDbJdbcTemplate;

    @Autowired(required = false)
    @Qualifier("MULTI_REGION_DB_TEMPLATE")
    NamedParameterJdbcTemplate multiRegionTemplate;

    @Autowired(required = false)
    @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
    NamedParameterJdbcTemplate multiRegionReplicaTemplate;


    @Autowired(required = false)
    @Qualifier("GEO_PARTITIONED_DB_TEMPLATE")
    NamedParameterJdbcTemplate geoDbJdbcTemplate;


    public NamedParameterJdbcTemplate resolve(TradeXDataSourceType dbType) {
        return switch (dbType) {
            case SINGLE_REGION_MULTI_ZONE -> singleDbJdbcTemplate;
            case MULTI_REGION_MULTI_ZONE -> multiRegionTemplate;
            case MULTI_REGION_READ_REPLICA -> multiRegionReplicaTemplate;
            case GEO_PARTITIONED -> geoDbJdbcTemplate;
            //case LOCAL -> localJdbcTemplate;
            //default -> throw new IllegalStateException("Unexpected value: " + dbType);
        };
    }
}
