package com.yugabyte.samples.tradex.api.domain.business;

import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Portfolio {
    AppUserId userId;
    String email;
    List<TradeOrderPerformance> recentTrades = new ArrayList<>();
}
