package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.StockInfoService;
import com.yugabyte.samples.tradex.api.service.TradeService;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.utils.QueryStatsProvider;
import com.yugabyte.samples.tradex.api.utils.Sql.Trade;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.dto.TradeOrderRequest;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@CrossOrigin
public class TradeController extends BaseController {

  private final TradeService tradeService;
  private final StockInfoService stockInfoService;
  private final QueryStatsProvider queryStatsProvider;

  public TradeController(UserService userService,
    ConnectionInfoRepo connectionInfoRepo,
    TradeXDBTypeContext tradeXDBTypeContext,
    TradeService tradeService,
    StockInfoService stockInfoService,
    QueryStatsProvider queryStatsProvider) {
    super(userService, connectionInfoRepo, tradeXDBTypeContext);
    this.tradeService = tradeService;
    this.stockInfoService = stockInfoService;
    this.queryStatsProvider = queryStatsProvider;
  }

  @GetMapping("/api/trades")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult fetchMyTrades(
    @RequestParam(name = "prevId", required = false, defaultValue = "0") int prevId,
    @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {

    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUser = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUser.getId()
      .getPreferredRegion());

    Instant start = Instant.now();
    List<TradeOrder> tradeOrders = tradeService.fetchByUserAndLimit(dbType, appUser.getId()
      .getId(), prevId, limit, appUser.getId()
      .getPreferredRegion());
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pUserId", appUser.getId()
      .getId());
    parameters.addValue("prevId", prevId);
    parameters.addValue("plimit", limit);
    parameters.addValue("prefRegion", appUser.getId()
      .getPreferredRegion());

    return queryStatsProvider.loadQueryStats(dbType, tradeOrders, inspectQueries, parameters,
      Trade.FETCH_USER_TRADES, timeElapsed, connectionInfo);
  }

  @PostMapping("/api/trades")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult addNewTrade(@RequestBody TradeOrderRequest newTradeOrder,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {

    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUser = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUser.getId()
      .getPreferredRegion());

    Instant start = Instant.now();

    TradeXStock stock = stockInfoService.getStock(dbType, newTradeOrder.getSymbol(), false);
    Integer savedTradeId = tradeService.save(dbType, newTradeOrder, stock.getId(),
      stock.getClosePrice(), appUser.getId()
        .getId(), appUser.getId()
        .getPreferredRegion());
    log.info("Saved new trade entry: {}", savedTradeId);
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("userId", appUser.getId()
      .getId());
    parameters.addValue("symbolId", stock.getId());
    parameters.addValue("tradeType", newTradeOrder.getTradeType()
      .name());
    parameters.addValue("bidPrice", stock.getClosePrice());
    parameters.addValue("stockUnits", newTradeOrder.getInvestAmount()
      .divide(stock.getClosePrice(), 3, RoundingMode.HALF_UP));
    parameters.addValue("payMethod", newTradeOrder.getPayMethod()
      .name());
    parameters.addValue("order_time", LocalDateTime.now());
    parameters.addValue("preferredRegion", appUser.getId()
      .getPreferredRegion());

    return queryStatsProvider.loadQueryStats(dbType, savedTradeId, inspectQueries, parameters,
      Trade.FETCH_USER_TRADES, timeElapsed, connectionInfo);

  }
}
