package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.StockPerformanceEntry;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ExplainQueryRepo;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.StockInfoService;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.utils.SqlProvider;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yugabyte.samples.tradex.api.utils.SqlQueries.StockSql.*;

@RestController
@CrossOrigin
@Slf4j
public class StockInfoController {

    @Autowired
    StockInfoService stockInfoService;

    @Autowired
    UserService userService;

    @Autowired
    ExplainQueryRepo explainQueryRepo;

    @Autowired
    ConnectionInfoRepo connectionInfoRepo;

    @Autowired
    SqlProvider sqlProvider;


    @GetMapping("/api/stocks/{symbol}")
    @Operation(summary = "Fetch Stock Info from yahoo")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult fetchSingleStock(@PathVariable("symbol") String symbol,
                                              @RequestParam(name = "hist", required = false, defaultValue = "N") boolean includeHist,
                                              @RequestParam(name = "from", required = false) String fromDate,
                                              @RequestParam(name = "to", required = false) String toDate,
                                              @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                      required = false, defaultValue = "false") Boolean inspectQueries, Authentication authentication
    ) {

        TradeXStock data;
        Instant start = Instant.now();

        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser user = userService.findByEmail(dbType, userDetails.getUsername()).get();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, user.getId().getPreferredRegion());
        if (StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate)) {
            data = stockInfoService.getStock(dbType, symbol, includeHist);
        } else {
            data = stockInfoService.getHistoricalTradeXStock(dbType, symbol, fromDate, toDate);
        }

        String query = sqlProvider.getStockSQL(STOCK_BY_SYMBOL_SQL); //"select * from trade_symbol ts where ts.symbol = :psymbol";
        Map<String, Object> parameters = Map.of("psymbol", symbol);

        List<String> analyzeQuery = Collections.emptyList();
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, query, parameters);
        }

        return new DBOperationResult(data, List.of("Executing ( " + dbType + " ) > " + query, parameters.toString()),
                analyzeQuery, Duration.between(start, Instant.now()).toMillis(), connectionInfo);

    }


    @GetMapping("/api/stocks")
    @Operation(summary = "Fetch Multiple Stock Info from yahoo")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult fetchAllStocks(Authentication authentication,
                                            @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                    required = false, defaultValue = "false") Boolean inspectQueries) {

        List<StockPerformanceEntry> data;
        Instant start = Instant.now();

        try {

            TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            AppUser user = userService.findByEmail(dbType, userDetails.getUsername()).get();
            ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, user.getId().getPreferredRegion());

            data = stockInfoService.fetchAllActiveStocksPerformance();

            String query = sqlProvider.getStockSQL(ALL_ACTIVE_STOCKS); //"select * from trade_symbol ts where ts.enabled=true";
            Map<String, Object> parameters = new HashMap<>(0);

            List<String> analyzeQuery = Collections.emptyList();
            if (inspectQueries) {
                analyzeQuery = explainQueryRepo.analyzeQuery(dbType, query, parameters);
            }


            return new DBOperationResult(data, List.of("Executing ( " + dbType + " ) > " + query, parameters.toString()),
                    analyzeQuery, Duration.between(start, Instant.now()).toMillis(), connectionInfo);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to fetch data for stock symbol. {}", e.getMessage());
            throw e;
        }

    }


    @GetMapping("/api/favstocks")
    @Operation(summary = "Fetch Multiple Stock Info from yahoo")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult fetchFavStocks(Authentication authentication,
                                            @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                    required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser appUser = userService.findByEmail(dbType, userDetails.getUsername()).get();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, appUser.getId().getPreferredRegion());

        List<StockPerformanceEntry> data;
        Instant start = Instant.now();


        Integer[] favs = appUser.getFavourites();
        data = stockInfoService.fetchFavStocksPerformance(favs);


        try {

            String query = sqlProvider.getStockSQL(APP_USER_FAV_STOCKS);
//      "select ts.* from trade_symbol ts, app_user au  where au.id = :userId " +
//        "and au.preferred_region = :prefRegion and ts.enabled = true " +
//        "and ts.trade_symbol_id = any(au.favourites)";

            Map<String, Object> params = new HashMap<>(2);
            params.put("userId", appUser.getId().getId());
            params.put("prefRegion", appUser.getId().getPreferredRegion());

            List<String> analyzeQuery = Collections.emptyList();
            if (inspectQueries) {
                analyzeQuery = explainQueryRepo.analyzeQuery(dbType, query, params);
            }

            return new DBOperationResult(data, List.of("Executing ( " + dbType + " ) > " + query, params.toString()),
                    analyzeQuery, Duration.between(start, Instant.now()).toMillis(), connectionInfo);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to fetch data for stock symbol. {}", e.getMessage());
            throw e;
        }

    }
}
