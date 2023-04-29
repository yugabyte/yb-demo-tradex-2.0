package com.yugabyte.samples.tradex.api.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSymbol {
    Integer id;
    String symbol;
    String company;
    String exchange;
    boolean enabled;
    Instant created_date;
}
