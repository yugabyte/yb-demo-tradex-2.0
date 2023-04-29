package com.yugabyte.samples.tradex.api.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class TradeXStock {
    private Integer id;
    private String symbol;
    private String name;
    private String currency;
    private String company;
    private String stockExchange;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal lowPrice;
    private BigDecimal highPrice;
    private BigDecimal marketCap;
    private BigDecimal volume;
    private BigDecimal avgVolume;
    private boolean enabled;
    private Instant createdDate;
    private Instant priceTime;
    private List<TradeXHistoricalQuote> historicalQuoteList;
}
