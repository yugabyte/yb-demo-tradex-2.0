package com.yugabyte.samples.tradex.api;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.service.ApplicationServiceException;
import com.yugabyte.samples.tradex.api.service.RefDataService;
import com.yugabyte.samples.tradex.api.service.StockInfoService;
import com.yugabyte.samples.tradex.api.service.TradeService;
import com.yugabyte.samples.tradex.api.utils.Sql;
import com.yugabyte.samples.tradex.api.web.utils.TradesGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupTask implements CommandLineRunner {

  private final List<Integer> sampleStockList = List.of(43, 67, 5, 66, 70, 22);
  private final RefDataService refDataService;
  private final StockInfoService stockInfoService;
  private final TradeService tradeService;
  private final NamedParameterJdbcTemplate jdbcTemplate;
  final TradesGenerator generator;

  @Value("${app.load_mock_data}")
  boolean loadMockData;

  @Value("${app.mylocation}")
  String location;

  @Value("${app.datasource_types}")
  TradeXDataSourceType[] tradeXInputDataSourceTypes;

  public StartupTask(RefDataService refDataService, StockInfoService stockInfoService,
    TradeService tradeService, NamedParameterJdbcTemplate jdbcTemplate, TradesGenerator generator) {
    this.refDataService = refDataService;
    this.stockInfoService = stockInfoService;
    this.tradeService = tradeService;
    this.jdbcTemplate = jdbcTemplate;
    this.generator = generator;
  }

  @Override
  public void run(String... args) throws Exception {
    Instant start = Instant.now();
    log.info(
      "Application started. Number of REF_DATA entries: {}", jdbcTemplate.queryForObject(
        Sql.RefData.CHECK_SQL,
        new HashMap<>(0), Integer.class));
    try {

      if (loadMockData) {
        log.info("Loading metadata - begin");
        stockInfoService.loadStockPerformance(false);
        for (TradeXDataSourceType t : tradeXInputDataSourceTypes) {
          refDataService.getDBNodes(t);
          refDataService.getDbClusterTypes(t);
          refDataService.getTrafficLocations(t);

          // generate few trade orders
          if ("BOSTON".equalsIgnoreCase(location)) {
            List<TradeOrder> user1Trades = generator.generateTrades(354, 1, "us-east-1",
              sampleStockList);
            List<TradeOrder> user2Trades = generator.generateTrades(354, 2, "us-east-1",
              sampleStockList);
            tradeService.deleteAllUserTrades(t, 1);
            tradeService.deleteAllUserTrades(t, 2);
            tradeService.insertTrades(t, user1Trades, 1, "us-east-1");
            tradeService.insertTrades(t, user2Trades, 2, "us-east-1");
          }
          if ("WASHINGTON".equalsIgnoreCase(location)) {
            List<TradeOrder> user1Trades = generator.generateTrades(354, 1, "us-west-2",
              sampleStockList);
            List<TradeOrder> user2Trades = generator.generateTrades(354, 2, "us-west-2",
              sampleStockList);
            tradeService.deleteAllUserTrades(t, 1);
            tradeService.deleteAllUserTrades(t, 2);
            tradeService.insertTrades(t, user1Trades, 1, "us-west-2");
            tradeService.insertTrades(t, user2Trades, 2, "us-west-2");
          }
        }
        refDataService.getNodeLocations();

        log.info("Loading metadata - complete in {} millisecs",
          Duration.between(start, Instant.now())
            .toMillis());

      }

      //load Stock Cache

    } catch (ApplicationServiceException e) {
      log.error("Error in initializing ref data", e);
    }
  }


}
