package com.yugabyte.samples.tradex.api.domain.db;

import com.yugabyte.samples.tradex.api.utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeOrder {
    private Integer id;
    private Integer userId;
    private TradeXStock stock;
    private String tradeType;
    private Instant orderTime;
    private Double bidPrice;
    private AppConstants.PayMethod payMethod;
    private BigDecimal stockUnits;
}
