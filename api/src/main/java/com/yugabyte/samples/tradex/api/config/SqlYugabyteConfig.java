package com.yugabyte.samples.tradex.api.config;

import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

@Configuration
@Slf4j
@Profile("yugabyte")
public class SqlYugabyteConfig {
  @Value("classpath:/db/queries/yugabyte/user-queries.xml")
  Resource userSqlFile;

  @Value("classpath:/db/queries/yugabyte/trade-queries.xml")
  Resource tradeSqlFile;
  @Value("classpath:/db/queries/yugabyte/chart-queries.xml")
  Resource chartSqlFile;
  @Value("classpath:/db/queries/yugabyte/stocks-queries.xml")
  Resource stockSqlFile;

  @Value("classpath:/db/queries/yugabyte/refdata-queries.xml")
  Resource refDataSqlFile;


  @Bean
  @Qualifier("USER_SQL")
  public Properties userSqls() throws IOException {
    return load(userSqlFile);
  }

  @Bean
  @Qualifier("TRADES_SQL")
  public Properties tradeSqls() throws IOException {
    return load(tradeSqlFile);
  }

  @Bean
  @Qualifier("CHART_SQL")
  public Properties chartSqls() throws IOException {
    return load(chartSqlFile);
  }

  @Bean
  @Qualifier("STOCK_SQL")
  public Properties stockSqls() throws IOException {
   return load(stockSqlFile);
  }

  @Bean
  @Qualifier("REFDATA_SQL")
  public Properties refDataSqls() throws IOException {
    return load(refDataSqlFile);
  }
  private Properties load(Resource resource) throws IOException {
    Properties properties = new Properties();
    properties.loadFromXML(resource.getInputStream());
    log.debug("Yugabyte Sql Props loaded: {}", properties.stringPropertyNames());
    return properties;
  }
}
