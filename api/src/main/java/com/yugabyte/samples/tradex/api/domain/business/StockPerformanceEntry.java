package com.yugabyte.samples.tradex.api.domain.business;

import lombok.Data;

import java.util.List;

@Data
public class StockPerformanceEntry {
    Integer stockId;
    String stockSymbol;
    String stockCompany;
    double currentPrice;
    boolean profit;
    double profitPercent;
    List<Double> trend;
}
