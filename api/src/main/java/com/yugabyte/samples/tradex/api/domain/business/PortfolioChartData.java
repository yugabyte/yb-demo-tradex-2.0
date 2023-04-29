package com.yugabyte.samples.tradex.api.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioChartData {
    Double totalValue;
    Map<String, List<PortfolioPerfChartEntry>> chartValues;
}
