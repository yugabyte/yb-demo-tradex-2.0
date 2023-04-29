package com.yugabyte.samples.tradex.api.domain.db;

import lombok.Data;

import java.time.Instant;

@Data
public class TradeSymbol {

    private Integer id;
    private String symbol;
    private String company;
    private String exchange;
    private Boolean enabled;
    private Instant createdDate;

}
