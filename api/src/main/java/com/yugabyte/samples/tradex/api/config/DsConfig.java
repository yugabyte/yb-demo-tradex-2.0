package com.yugabyte.samples.tradex.api.config;

import static com.yugabyte.samples.tradex.api.config.TradeXDataSourceType.GEO_PARTITIONED;
import static com.yugabyte.samples.tradex.api.config.TradeXDataSourceType.MULTI_REGION_MULTI_ZONE;
import static com.yugabyte.samples.tradex.api.config.TradeXDataSourceType.MULTI_REGION_READ_REPLICA;
import static com.yugabyte.samples.tradex.api.config.TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@Slf4j
public class DsConfig {

  @Bean
  @Profile("SR & yugabyte")
  @ConfigurationProperties("spring.datasource.sr")
  public DataSourceProperties srDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Profile("SR & yugabyte")
  public DataSource srDataSource() {
    return srDataSourceProperties().initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("SR & yugabyte")
  @Qualifier("SINGLE_DB_TEMPLATE")
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Primary
  public NamedParameterJdbcTemplate srJdbcTemplate() {
    return new NamedParameterJdbcTemplate(srDataSource());
  }


  @Bean
  @Profile("MR & yugabyte")
  @ConfigurationProperties("spring.datasource.mr")
  public DataSourceProperties mrDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Profile("MR & yugabyte")
  public DataSource mrDataSource() {
    return mrDataSourceProperties().initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("MR & yugabyte")
  @Qualifier("MULTI_REGION_DB_TEMPLATE")
  @Order(Ordered.HIGHEST_PRECEDENCE + 2)
  public NamedParameterJdbcTemplate mrJdbcTemplate() {
    return new NamedParameterJdbcTemplate(mrDataSource());
  }


  @Bean
  @Profile("MRRR & yugabyte")
  @ConfigurationProperties("spring.datasource.mrrr")
  public DataSourceProperties mrrrDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Profile("MRRR & yugabyte")
  public DataSource mrrrDataSource() {
    return mrrrDataSourceProperties().initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("MRRR & yugabyte")
  @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
  @Order(Ordered.HIGHEST_PRECEDENCE + 2)
  public NamedParameterJdbcTemplate mrrrJdbcTemplate() {
    return new NamedParameterJdbcTemplate(mrrrDataSource());
  }


  @Bean
  @Profile("GEO & yugabyte")
  @ConfigurationProperties("spring.datasource.geo")
  public DataSourceProperties geoDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Profile("GEO & yugabyte")
  public DataSource geoDataSource() {
    return geoDataSourceProperties().initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("GEO & yugabyte")
  @Qualifier("GEO_PARTITIONED_DB_TEMPLATE")
  @Order(Ordered.HIGHEST_PRECEDENCE + 4)
  public NamedParameterJdbcTemplate geoJdbcTemplate() {
    return new NamedParameterJdbcTemplate(geoDataSource());
  }

  @Bean
  @Profile("SR & oracle")
  @ConfigurationProperties("spring.datasource.srora")
  public DataSourceProperties srOraDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Profile("SR & oracle")
  public DataSource srOraDataSource() {
    return srOraDataSourceProperties().initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("SR & oracle")
  @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
  @Order(Ordered.HIGHEST_PRECEDENCE + 5)
  public NamedParameterJdbcTemplate srOraJdbcTemplate() {
    return new NamedParameterJdbcTemplate(srOraDataSource());
  }

  @Bean
  public TradeXDataSourceType[] availableDataSourceTypes(List<DataSource> dataSources) {
    Set<TradeXDataSourceType> types = new HashSet<>(dataSources.size());
    for (DataSource ds : dataSources) {
      if (ds == geoDataSource()) {
        types.add(GEO_PARTITIONED);
      } else if (ds == mrDataSource()) {
        types.add(MULTI_REGION_MULTI_ZONE);
      } else if (ds == srDataSource()) {
        types.add(SINGLE_REGION_MULTI_ZONE);
      } else if (ds == mrrrDataSource()) {
        types.add(MULTI_REGION_READ_REPLICA);
      }
    }
    return types.toArray(new TradeXDataSourceType[0]);
  }
}

