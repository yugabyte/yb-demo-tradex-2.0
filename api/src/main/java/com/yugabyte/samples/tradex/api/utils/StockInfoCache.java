package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.domain.business.StockPerformance;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StockInfoCache {
    Map<Integer, List<Double>> trendCache = new HashMap<>();
    Map<String, StockPerformance> stockPerformanceMap = new HashMap<>();

    public void addStockTrend(Integer key, List<Double> trend) {
        trendCache.put(key, trend);
    }

    public void clearTrendCache() {
        trendCache.clear();
    }

    public List<Double> fetchTrend(Integer key) {
        return trendCache.get(key);
    }

    public boolean containsTrend(Integer key) {
        return trendCache.containsKey(key);
    }

    public Map<Integer, List<Double>> fetchTrendMap() {
        return trendCache;
    }

    public void addStockPerformance(String key, StockPerformance stockItem) {
        stockPerformanceMap.put(key, stockItem);
    }

    public void clearStockPerfMap() {
        stockPerformanceMap.clear();
    }

    public StockPerformance fetchStockPerformance(String key) {
        return stockPerformanceMap.get(key);
    }

    public List<StockPerformance> fetchAllStocksPerformance() {
        return stockPerformanceMap.values().stream().toList();
    }

}
