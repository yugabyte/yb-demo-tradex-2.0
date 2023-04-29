package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ExplainQueryRepo;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.StockInfoService;
import com.yugabyte.samples.tradex.api.service.TradeService;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.utils.SqlProvider;
import com.yugabyte.samples.tradex.api.utils.SqlQueries.TradeSql;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.dto.TradeOrderRequest;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
public class TradeController {

    @Autowired
    TradeService tradeService;
    @Autowired
    ExplainQueryRepo explainQueryRepo;
    @Autowired
    StockInfoService stockInfoService;
    @Autowired
    UserService userService;
    @Autowired
    ConnectionInfoRepo connectionInfoRepo;
    @Autowired
    SqlProvider sqlProvider;

    @GetMapping("/api/trades")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult fetchMyTrades(@RequestParam(name = "prevId", required = false,
            defaultValue = "0") int prevId, @RequestParam(name = "limit", required = false,
            defaultValue = "10") int limit, Authentication authentication,
                                           @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                   required = false, defaultValue = "false") Boolean inspectQueries) {

        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        AppUser appUserFromDB = userService.findByEmail(dbType, userDetails.getUsername()).get();
        AppUserId userId = appUserFromDB.getId();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, userId.getPreferredRegion());
        Instant start = Instant.now();
        List<TradeOrder> tradeOrders = tradeService.fetchByUserAndLimit(dbType, userId.getId(), prevId, limit,
                userId.getPreferredRegion());

        Map<String, Object> parameters = Map.of("pUserId", userId.getId(),
                "prevId", 0,
                "plimit", limit,
                "prefRegion", userId.getPreferredRegion());

        List<String> analyzeQuery = Collections.emptyList();

        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getTradeSQL(TradeSql.FETCH_USER_TRADES), parameters);
        }

        return new DBOperationResult(tradeOrders, List.of("Executing ( " + TradeXDBTypeContext.getDbType() + " ) > " + sqlProvider.getTradeSQL(TradeSql.FETCH_USER_TRADES),
                parameters.toString()), analyzeQuery, Duration.between(start, Instant.now()).toMillis(), connectionInfo);
    }

    @PostMapping("/api/trades")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult addNewTrade(@RequestBody TradeOrderRequest newTradeOrder, Authentication authentication,
                                         @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                 required = false, defaultValue = "false") Boolean inspectQueries) {

        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        AppUser appUserFromDB = userService.findByEmail(dbType, userDetails.getUsername()).get();
        AppUserId userId = appUserFromDB.getId();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, userId.getPreferredRegion());
        Instant start = Instant.now();

        TradeXStock stock = stockInfoService.getStock(dbType, newTradeOrder.getSymbol(), false);
        Integer savedTradeId = tradeService.save(dbType, newTradeOrder, stock.getId(),
                stock.getClosePrice(), userId.getId(), userId.getPreferredRegion());
        log.info("Saved new trade entry: {}", savedTradeId);

        Map parameters = new HashMap();
        parameters.put("userId", userId.getId());
        parameters.put("symbolId", stock.getId());
        parameters.put("tradeType", newTradeOrder.getTradeType().name());
        parameters.put("bidPrice", stock.getClosePrice());
        parameters.put("stockUnits", newTradeOrder.getInvestAmount().divide(stock.getClosePrice(), 3, RoundingMode.HALF_UP));
        parameters.put("payMethod", newTradeOrder.getPayMethod().name());
        parameters.put("order_time", LocalDateTime.now());
        parameters.put("preferredRegion", userId.getPreferredRegion());

        List<String> analyzeQuery = Collections.emptyList();

        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getTradeSQL(TradeSql.INSERT_TRADE), parameters);
        }

        return new DBOperationResult(savedTradeId,
                List.of("Executing ( " + dbType + " ) > " + sqlProvider.getTradeSQL(TradeSql.INSERT_TRADE), parameters.toString()),
                analyzeQuery, Duration.between(start, Instant.now()).toMillis(), connectionInfo);
    }
}
