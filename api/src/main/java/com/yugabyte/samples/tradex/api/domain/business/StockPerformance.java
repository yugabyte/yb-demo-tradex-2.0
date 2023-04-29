package com.yugabyte.samples.tradex.api.domain.business;

import com.yugabyte.samples.tradex.api.domain.db.TradeXStock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPerformance {
    TradeXStock stock;
    List<Double> trend;

}
