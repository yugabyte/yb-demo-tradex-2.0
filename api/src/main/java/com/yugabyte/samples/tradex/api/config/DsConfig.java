package com.yugabyte.samples.tradex.api.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@Slf4j
public class DsConfig {
  @Bean
  @Profile("MR & yugabyte")
  @ConfigurationProperties("spring.datasource.mr")
  public DataSourceProperties mrDataSourceProperties() {
    return new DataSourceProperties();
  }
  @Bean
  @Profile("MR & yugabyte")
  public DataSource mrDataSource() {
    return mrDataSourceProperties()
      .initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("MR & yugabyte")
  @Qualifier("MULTI_REGION_DB_TEMPLATE")
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
    return mrrrDataSourceProperties()
      .initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("MRRR & yugabyte")
  @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
  public NamedParameterJdbcTemplate mrrrJdbcTemplate() {
    return new NamedParameterJdbcTemplate(mrDataSource());
  }

  @Bean
  @Profile("SR & yugabyte")
  @ConfigurationProperties("spring.datasource.sr")
  public DataSourceProperties srDataSourceProperties() {
    return new DataSourceProperties();
  }
  @Bean
  @Profile("SR & yugabyte")
  public DataSource srDataSource() {
    return srDataSourceProperties()
      .initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("SR & yugabyte")
  @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
  public NamedParameterJdbcTemplate srJdbcTemplate() {
    return new NamedParameterJdbcTemplate(mrDataSource());
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
    return geoDataSourceProperties()
      .initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("GEO & yugabyte")
  @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
  public NamedParameterJdbcTemplate geoJdbcTemplate() {
    return new NamedParameterJdbcTemplate(mrDataSource());
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
    return srOraDataSourceProperties()
      .initializeDataSourceBuilder()
      .build();
  }

  @Bean
  @Profile("SR & oracle")
  @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
  public NamedParameterJdbcTemplate srOraJdbcTemplate() {
    return new NamedParameterJdbcTemplate(mrDataSource());
  }

}

