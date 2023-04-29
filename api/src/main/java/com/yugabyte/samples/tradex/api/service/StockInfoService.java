package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.StockPerformance;
import com.yugabyte.samples.tradex.api.domain.business.StockPerformanceEntry;
import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import com.yugabyte.samples.tradex.api.domain.repo.StockRepo;
import com.yugabyte.samples.tradex.api.utils.StockInfoCache;
import com.yugabyte.samples.tradex.api.utils.TradeXHistoryIntervalPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@Slf4j
public class StockInfoService {

    @Autowired
    StockRepo stockRepo;
    @Autowired
    StockInfoCache stockInfoCache;
    private boolean stockTrendLoaded;

    @Cacheable("SINGLE_STOCK")
    public TradeXStock getStock(TradeXDataSourceType dbType, String symbol, boolean includeHist) {

        TradeXStock tradeXStock = stockRepo.getTradeXStock(dbType, symbol);

        if (includeHist) {
            log.debug("Fetching history");
            tradeXStock.setHistoricalQuoteList(stockRepo.getTradeXHistory(dbType, tradeXStock.getId(), TradeXHistoryIntervalPeriod.ONE_DAY));
        }

        return tradeXStock;
    }

    @Cacheable("STOCK_SYMBOLS_SINGLE")
    public TradeXStock getStockSymbol(TradeXDataSourceType dbType, int symbolId) {
        return stockRepo.getStockSymbol(dbType, symbolId);
    }

    public TradeXStock getHistoricalTradeXStock(TradeXDataSourceType dbType, String symbol, String fromDate, String toDate) {
        TradeXStock tradeXStock = stockRepo.getTradeXStock(dbType, symbol);
        tradeXStock.setHistoricalQuoteList(stockRepo.getTradeXHistory(dbType, tradeXStock.getId(), fromDate, toDate));
        return tradeXStock;
    }

    @Cacheable("STOCK_PERF_TREND")
    public List<Double> fetchStockTrend(Integer symbolId) {
        log.debug("Stock Trend Cache for key: {}: {}", symbolId, stockInfoCache.fetchTrend(symbolId));
        return stockInfoCache.fetchTrend(symbolId);
    }

    public void loadStockTrendCache() {

        stockRepo.fetchStockTrends().forEach(e -> {
            stockInfoCache.addStockTrend(e.getFirst(), e.getSecond());
        });

        stockTrendLoaded = true;
        log.info("Stock Trend Cache updated");
    }

    public void loadStockPerformance(boolean cleanCache) {

        if (cleanCache) {
            stockInfoCache.clearTrendCache();
            stockInfoCache.clearStockPerfMap();
            stockTrendLoaded = false;
        }

        if (!stockTrendLoaded) {
            loadStockTrendCache();
            log.debug("Trend Map: {}", stockInfoCache.fetchTrendMap());
        }
        stockRepo.getAllActiveTradeXStocks(TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE).forEach(e -> {
            StockPerformance entry = new StockPerformance();
            entry.setStock(e);
            entry.setTrend(stockInfoCache.fetchTrend(e.getId()));
            stockInfoCache.addStockPerformance(e.getSymbol(), entry);
        });
    }


    public List<StockPerformanceEntry> fetchAllActiveStocksPerformance() {
        return stockInfoCache.fetchAllStocksPerformance().stream().map(e -> {
            StockPerformanceEntry entry = new StockPerformanceEntry();
            TradeXStock stock = e.getStock();
            entry.setStockId(stock.getId());
            entry.setStockSymbol(stock.getSymbol());
            entry.setStockCompany(stock.getCompany());
            entry.setCurrentPrice(stock.getHighPrice() == null ? 0.0 : stock.getHighPrice().doubleValue());
            if (e.getTrend() != null) {
                entry.setProfit(e.getTrend().get(0) > e.getTrend().get(1));
                entry.setProfitPercent(((e.getTrend().get(0) - e.getTrend().get(1)) / e.getTrend().get(1)) * 100);
                entry.setTrend(e.getTrend());
            } else {
                log.warn("Missing trend info for: {}", e.getStock().getSymbol());
                entry.setProfit(false);
                entry.setProfitPercent(0);
                entry.setTrend(Collections.emptyList());
            }

            return entry;
        }).collect(Collectors.toList());
    }

    public List<StockPerformanceEntry> fetchFavStocksPerformance(Integer[] favs) {

        if (null == favs) {
            return Collections.emptyList();
        }
        return stockInfoCache.fetchAllStocksPerformance().stream().filter(t -> Arrays.asList(favs).contains(t.getStock().getId())).map(e -> {
            StockPerformanceEntry entry = new StockPerformanceEntry();
            TradeXStock stock = e.getStock();
            entry.setStockId(stock.getId());
            entry.setStockSymbol(stock.getSymbol());
            entry.setStockCompany(stock.getCompany());
            entry.setCurrentPrice(stock.getHighPrice() == null ? 0.0 : stock.getHighPrice().doubleValue());
            entry.setProfit(e.getTrend().get(0) > e.getTrend().get(1));
            entry.setProfitPercent(((e.getTrend().get(0) - e.getTrend().get(1)) / e.getTrend().get(1)) * 100);
            entry.setTrend(e.getTrend());
            return entry;
        }).collect(Collectors.toList());
    }
}
