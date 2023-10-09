package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.StockPerformanceEntry;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.StockInfoService;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.utils.QueryStatsProvider;
import com.yugabyte.samples.tradex.api.utils.Sql.Stock;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j
public class StockInfoController extends BaseController {

  @Autowired
  StockInfoService stockInfoService;
  @Autowired
  UserService userService;
  @Autowired
  ConnectionInfoRepo connectionInfoRepo;
  @Autowired
  QueryStatsProvider enhancer;

  @GetMapping("/api/stocks/{symbol}")
  @Operation(summary = "Fetch Stock Info from yahoo")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult fetchSingleStock(@PathVariable("symbol") String symbol,
    @RequestParam(name = "hist", required = false, defaultValue = "N") boolean includeHist,
    @RequestParam(name = "from", required = false) String fromDate,
    @RequestParam(name = "to", required = false) String toDate,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries,
    Authentication authentication) {

    TradeXStock data;
    Instant start = Instant.now();

    TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
    AppUser user = fetchUser(authentication);
    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, user.getId()
      .getPreferredRegion());
    if (StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate)) {
      data = stockInfoService.getStock(dbType, symbol, includeHist);
    } else {
      data = stockInfoService.getHistoricalTradeXStock(dbType, symbol, fromDate, toDate);
    }
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("psymbol", symbol);

    return enhancer.loadQueryStats(dbType, data, inspectQueries, parameters,
      Stock.STOCK_BY_SYMBOL_SQL, timeElapsed, connectionInfo);

  }


  @GetMapping("/api/stocks")
  @Operation(summary = "Fetch Multiple Stock Info from yahoo")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult fetchAllStocks(Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {

    List<StockPerformanceEntry> data;
    Instant start = Instant.now();

    try {

      TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
      AppUser user = fetchUser(authentication);
      ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, user.getId()
        .getPreferredRegion());

      data = stockInfoService.fetchAllActiveStocksPerformance();
      long timeElapsed = Duration.between(start, Instant.now())
        .toMillis();

      return enhancer.loadQueryStats(dbType, data, inspectQueries, new MapSqlParameterSource(),
        Stock.ALL_ACTIVE_STOCKS, timeElapsed, connectionInfo);

    } catch (Exception e) {
      log.error("Failed to fetch data for stock symbol. {}", e.getMessage());
      log.trace("Failed to fetch data for stock symbol. {}", e);
      throw e;
    }

  }


  @GetMapping("/api/favstocks")
  @Operation(summary = "Fetch Multiple Stock Info from yahoo")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult fetchFavStocks(Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {
    try {
      TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();

      AppUser appUser = fetchUser(authentication);
      ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType,
        appUser.getId()
          .getPreferredRegion());

      List<StockPerformanceEntry> data;
      Instant start = Instant.now();
      Integer[] favs = appUser.getFavourites();
      data = stockInfoService.fetchFavStocksPerformance(favs);
      long timeElapsed = Duration.between(start, Instant.now())
        .toMillis();

      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("userId", appUser.getId()
        .getId());
      params.addValue("prefRegion", appUser.getId()
        .getPreferredRegion());

      return enhancer.loadQueryStats(dbType, data, inspectQueries, params,
        Stock.APP_USER_FAV_STOCKS, timeElapsed, connectionInfo);

    } catch (Exception e) {
      log.error("Failed to fetch data for stock symbol. Message: {}", e.getMessage());
      log.trace("Failed to fetch data for stock symbol. Message: {}", e);
      throw e;
    }

  }
}
