package com.yugabyte.samples.tradex.api.domain.business;

import com.yugabyte.samples.tradex.api.domain.db.TradeOrder;
import lombok.Data;

import java.util.List;

@Data
public class TradeOrderPerformance {
    TradeOrder tradeOrder;
    Integer orderId;
    String stockSymbol;
    String stockCompany;
    double currentPrice;
    boolean profit;
    double profitPercent;
    List<Double> trend;
}
