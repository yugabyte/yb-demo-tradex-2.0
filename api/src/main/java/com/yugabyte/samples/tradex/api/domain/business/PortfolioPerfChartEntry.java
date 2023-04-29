package com.yugabyte.samples.tradex.api.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioPerfChartEntry {
    String label;
    double amount;
    Instant timestamp;
    String intervalPeriod;
}
