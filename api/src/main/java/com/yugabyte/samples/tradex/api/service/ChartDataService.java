package com.yugabyte.samples.tradex.api.service;

import static java.util.stream.Collectors.groupingBy;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.PortfolioChartData;
import com.yugabyte.samples.tradex.api.domain.business.PortfolioPerfChartEntry;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.domain.repo.ChartRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.utils.QueryStatsProvider;
import com.yugabyte.samples.tradex.api.utils.Sql;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ChartDataService {

  private static final String HOUR_LABEL = "HH:MM";
  private static final DateTimeFormatter HOUR_LABEL_FORMATTER = DateTimeFormatter.ofPattern(HOUR_LABEL)
    .withZone(ZoneId.systemDefault());
  private final ChartRepo chartRepo;
  private final ConnectionInfoRepo connectionInfoRepo;
  private final QueryStatsProvider queryStatsProvider;

  public ChartDataService(ChartRepo chartRepo, ConnectionInfoRepo connectionInfoRepo,
    QueryStatsProvider queryStatsProvider) {
    this.chartRepo = chartRepo;
    this.connectionInfoRepo = connectionInfoRepo;
    this.queryStatsProvider = queryStatsProvider;
  }

  @Transactional
  public DBOperationResult fetchPortfolioChart(TradeXDataSourceType dbType, AppUserId appUserId,
    boolean analyzeQueries) {
    log.debug("about to fetch portfolio chart entries for user: {}", appUserId.getId());
    Instant start = Instant.now();
    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType,
      appUserId.getPreferredRegion());
    Double total = chartRepo.fetchPortfolioTotalAmount(dbType, appUserId);

    PortfolioChartData result = new PortfolioChartData(total, new HashMap<>());
    List<PortfolioPerfChartEntry> portfolioChartEntries = chartRepo.fetchPortfolioChartValues(
      dbType, appUserId);

    long executionTime = Duration.between(start, Instant.now())
      .toMillis();
    if (portfolioChartEntries.isEmpty()) {
      log.debug("portfolioChart entries are empty");
      return new DBOperationResult(result, new ArrayList<>(), new ArrayList<>(), 0, connectionInfo);
    }

    result.setChartValues(portfolioChartEntries.stream()
      .map(this::updateLabels)
      .collect(groupingBy(PortfolioPerfChartEntry::getIntervalPeriod)));

    log.debug("fetched {} entries in portfolio chart", portfolioChartEntries.size());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", appUserId.getId());
    params.addValue("prefRegion", appUserId.getPreferredRegion());

    return queryStatsProvider.loadQueryStats(dbType, result, analyzeQueries, params,
      Sql.Charts.PORTFOLIO_CHART_SQL, executionTime, connectionInfo);
  }

  private PortfolioPerfChartEntry updateLabels(PortfolioPerfChartEntry input) {
    if (null == input || null == input.getTimestamp()) {
      return input;
    }

    switch (input.getIntervalPeriod()) {
      case "1H", "90MIN" -> input.setLabel(HOUR_LABEL_FORMATTER.format(input.getTimestamp()));
      case "1DAY" -> input.setLabel(input.getTimestamp()
        .atZone(ZoneId.systemDefault())
        .getDayOfWeek()
        .getDisplayName(TextStyle.SHORT, Locale.US));
      default -> {
      }
    }
    return input;
  }

}
