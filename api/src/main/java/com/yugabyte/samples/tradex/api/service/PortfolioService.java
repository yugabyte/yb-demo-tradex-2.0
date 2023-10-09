package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.Portfolio;
import com.yugabyte.samples.tradex.api.domain.business.TradeOrderPerformance;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PortfolioService {

  private final StockInfoService stockInfoService;
  private final TradeService tradeService;
  private final UserService userService;

  public PortfolioService(StockInfoService stockInfoService, TradeService tradeService,
    UserService userService) {
    this.stockInfoService = stockInfoService;
    this.tradeService = tradeService;
    this.userService = userService;
  }

  public Portfolio getPortfolio(TradeXDataSourceType dbType, String email) {

    Optional<AppUser> user = userService.findByEmail(dbType, email);

    int userId = user.get()
      .getId()
      .getId();
    String prefRegion = user.get()
      .getId()
      .getPreferredRegion();

    log.debug("resolved datasource type: {}", dbType);
    List<TradeOrder> recentTradeOrders = tradeService.fetchByUserAndLimit(dbType, userId, 0, 20,
      prefRegion);
    Portfolio userPortfolio = new Portfolio();
    userPortfolio.setUserId(user.get()
      .getId());
    userPortfolio.setEmail(user.get()
      .getEmail());
    userPortfolio.setRecentTrades(recentTradeOrders.stream()
      .map(e -> getTradePerformance(dbType, e))
      .collect(Collectors.toList()));
    log.debug("Fetched details: {}", userPortfolio);
    return userPortfolio;
  }

  private TradeOrderPerformance getTradePerformance(TradeXDataSourceType dbType,
    TradeOrder tradeOrder) {
    TradeOrderPerformance tradeOrderPerformance = new TradeOrderPerformance();
    TradeXStock stock = tradeOrder.getStock();
    tradeOrderPerformance.setStockCompany(stock.getCompany());
    tradeOrderPerformance.setOrderId(tradeOrder.getId());
    tradeOrderPerformance.setStockSymbol(stock.getSymbol());
    tradeOrderPerformance.setCurrentPrice(stock.getHighPrice() == null ? 0.0 : stock.getHighPrice()
      .doubleValue());
    tradeOrderPerformance.setProfit(tradeOrder.getBidPrice() < stock.getHighPrice()
      .doubleValue());
    tradeOrderPerformance.setProfitPercent((stock.getHighPrice()
                                              .doubleValue() - tradeOrder.getBidPrice()) / 100);
    tradeOrderPerformance.setTrend(stockInfoService.fetchStockTrend(tradeOrder.getStock()
      .getId()));

    return tradeOrderPerformance;
  }

}
