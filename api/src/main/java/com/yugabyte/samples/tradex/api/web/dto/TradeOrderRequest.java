package com.yugabyte.samples.tradex.api.web.dto;

import com.yugabyte.samples.tradex.api.utils.AppConstants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TradeOrderRequest {
    String symbol;
    AppConstants.PayMethod payMethod;
    BigDecimal investAmount;
    AppConstants.TradeType tradeType;
}
