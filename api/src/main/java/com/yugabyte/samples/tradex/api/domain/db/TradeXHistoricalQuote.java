package com.yugabyte.samples.tradex.api.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class TradeXHistoricalQuote {
    private String symbol;
    private Instant date;
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal high;
}
