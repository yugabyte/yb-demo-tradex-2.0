package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeXJdbcTemplateResolver {

  private final NamedParameterJdbcTemplate singleDbJdbcTemplate;

  private final NamedParameterJdbcTemplate multiRegionTemplate;

  private final NamedParameterJdbcTemplate multiRegionReplicaTemplate;


  private final NamedParameterJdbcTemplate geoDbJdbcTemplate;

  @Value("${app.datasource_types}")
  private TradeXDataSourceType[] tradeXInputDataSourceTypes;

  public TradeXJdbcTemplateResolver(
    @Qualifier("SINGLE_DB_TEMPLATE") NamedParameterJdbcTemplate singleDbJdbcTemplate,
    @Qualifier("MULTI_REGION_DB_TEMPLATE") NamedParameterJdbcTemplate multiRegionTemplate,
    @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE") NamedParameterJdbcTemplate multiRegionReplicaTemplate,
    @Qualifier("GEO_PARTITIONED_DB_TEMPLATE") NamedParameterJdbcTemplate geoDbJdbcTemplate) {
    this.singleDbJdbcTemplate = singleDbJdbcTemplate;
    this.multiRegionTemplate = multiRegionTemplate;
    this.multiRegionReplicaTemplate = multiRegionReplicaTemplate;
    this.geoDbJdbcTemplate = geoDbJdbcTemplate;
  }


  public NamedParameterJdbcTemplate resolve(TradeXDataSourceType dbType) {
    var d = (dbType == null) ? tradeXInputDataSourceTypes[0] : dbType;
    return switch (d) {
      case SINGLE_REGION_MULTI_ZONE -> singleDbJdbcTemplate;
      case MULTI_REGION_MULTI_ZONE -> multiRegionTemplate;
      case MULTI_REGION_READ_REPLICA -> multiRegionReplicaTemplate;
      case GEO_PARTITIONED -> geoDbJdbcTemplate;
    };
  }
}
