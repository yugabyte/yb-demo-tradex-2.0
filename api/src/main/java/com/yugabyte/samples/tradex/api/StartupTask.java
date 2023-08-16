package com.yugabyte.samples.tradex.api;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.service.ApplicationServiceException;
import com.yugabyte.samples.tradex.api.service.RefDataService;
import com.yugabyte.samples.tradex.api.service.StockInfoService;
import com.yugabyte.samples.tradex.api.service.TradeService;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import com.yugabyte.samples.tradex.api.web.utils.TradesGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class StartupTask implements CommandLineRunner {

    static final String CHECK_SQL = "select count(*) from REF_DATA";
    final List<Integer> sampleStockList = List.of(43, 67, 5, 66, 70, 22);
    @Autowired
    RefDataService dataService;
    @Autowired
    StockInfoService stockInfoService;
    @Autowired
    TradeService tradeService;
    @Autowired
    TradeXJdbcTemplateResolver jdbcTemplateResolver;
    @Value("${app.load_mock_data}")
    boolean loadRequired;
    @Value("${app.mylocation}")
    String location;
    @Autowired
    TradesGenerator generator;

    @Override
    public void run(String... args) throws Exception {
        Instant start = Instant.now();
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE);
        log.info("Application started. Number of REF_DATA entries: "
                + template.queryForObject(CHECK_SQL, new HashMap<>(0), Integer.class));
        try {

            if (loadRequired) {
                log.info("Loading metadata - begin");
                stockInfoService.loadStockPerformance(false);
                List<TradeOrder> user1Trades = generator.generateTrades(354, 1, "boston", sampleStockList);
                List<TradeOrder> user2Trades = generator.generateTrades(354, 2, "boston", sampleStockList);
                for (TradeXDataSourceType t : TradeXDataSourceType.values()) {
                    dataService.getDBNodes(t);
                    dataService.getDbClusterTypes(t);
                    dataService.getTrafficLocations(t);

                    // generate few trade orders
                    if ("BOSTON".equalsIgnoreCase(location)) {
                        tradeService.deleteAllUserTrades(t, 1);
                        tradeService.deleteAllUserTrades(t, 2);
                        tradeService.insertTrades(t, user1Trades, 1, "boston");
                        tradeService.insertTrades(t, user2Trades, 2, "boston");
                    }
                }
                dataService.getNodeLocations();

                log.info("Loading metadata - complete in {} millisecs", Duration.between(start, Instant.now()).toMillis());

            }

            //load Stock Cache


        } catch (ApplicationServiceException e) {
            e.printStackTrace();
        }
    }


}
