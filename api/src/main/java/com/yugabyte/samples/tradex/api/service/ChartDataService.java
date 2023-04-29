package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.PortfolioChartData;
import com.yugabyte.samples.tradex.api.domain.business.PortfolioPerfChartEntry;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.domain.repo.ChartRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ExplainQueryRepo;
import com.yugabyte.samples.tradex.api.utils.QueryParamDisplayUtils;
import com.yugabyte.samples.tradex.api.utils.SqlProvider;
import com.yugabyte.samples.tradex.api.utils.SqlQueries;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ChartDataService {
    //  final String PORTFOLIO_CHART_SQL = """
//      select price_time ,  trunc ( sum(cs.units * q.high_price), 2 ) as total,
//        q.interval_period from current_stocks_v cs , stock_period_info_v q
//        where cs.user_id =:userId and cs.preferred_region =:prefRegion and cs.symbol_id = q.trade_symbol_id
//        group by price_time ,interval_period order by price_time desc , interval_period
//    """;
//  final String PORTFOLIO_TOTAL = """
//    select coalesce (sum( units * ts.high_price), 0.0) from current_stocks_v csv2 ,
//     trade_symbol ts where csv2.symbol_id = ts.trade_symbol_id and csv2.user_id = :userId and csv2.preferred_region =:prefRegion
//    """;
    final String HOUR_LABEL = "HH:MM";
    final DateTimeFormatter HOUR_LABEL_FORMATTER = DateTimeFormatter.ofPattern(HOUR_LABEL).withZone(ZoneId.systemDefault());
    @Autowired
    ChartRepo chartRepo;
    @Autowired
    ExplainQueryRepo explainQueryRepo;
    @Autowired
    ConnectionInfoRepo connectionInfoRepo;

    @Autowired
    SqlProvider sqlProvider;

    @Transactional
    public DBOperationResult fetchPortfolioChart(TradeXDataSourceType dbType, AppUserId appUserId, boolean analyzeQueries) {
        log.debug("about to fetch portfolio chart entries for user: {}", appUserId.getId());
        Instant start = Instant.now();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, appUserId.getPreferredRegion());
        Double total = chartRepo.fetchPortfolioTotalAmount(dbType, appUserId);

        Map<String, List<PortfolioPerfChartEntry>> emptyResults = getEmptyResult();

        PortfolioChartData result = new PortfolioChartData(total, emptyResults);
        List<PortfolioPerfChartEntry> portfolioChartEntries = chartRepo.fetchPortfolioChartValues(dbType, appUserId);

        if (portfolioChartEntries.isEmpty()) {
            log.debug("portfolioChart entries are empty");
            return new DBOperationResult(result, new ArrayList<>(), new ArrayList<>(), 0, connectionInfo);
        }

        result.setChartValues(portfolioChartEntries.stream().map(this::updateLabels)
                .collect(groupingBy(PortfolioPerfChartEntry::getIntervalPeriod)));
        log.debug("fetched {} entries in portfolio chart", portfolioChartEntries.size());

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", appUserId.getId());
        params.addValue("prefRegion", appUserId.getPreferredRegion());

        List<String> analyzeQuery = Collections.emptyList();
        if (analyzeQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getChartSQL(SqlQueries.ChartSql.PORTFOLIO_CHART_SQL), params);
        }

        return new DBOperationResult(result,
                List.of("Executing ( " + dbType + " ) > " + sqlProvider.getChartSQL(SqlQueries.ChartSql.PORTFOLIO_CHART_SQL),
                        QueryParamDisplayUtils.getParameters(params)),
                analyzeQuery, Duration.between(start, Instant.now()).toMillis(), connectionInfo);

    }

    private Map<String, List<PortfolioPerfChartEntry>> getEmptyResult() {
        Map<String, List<PortfolioPerfChartEntry>> emptyResults = new HashMap<>();
        //'1H', '90MIN', '1DAY', '1WEEK', '3MONTH'
        emptyResults.put("1H", new ArrayList<>(0));
        emptyResults.put("90MIN", new ArrayList<>(0));
        emptyResults.put("1DAY", new ArrayList<>(0));
        emptyResults.put("1WEEK", new ArrayList<>(0));
        emptyResults.put("3MONTH", new ArrayList<>(0));
        return emptyResults;
    }


    private PortfolioPerfChartEntry updateLabels(PortfolioPerfChartEntry input) {
        if (null == input || null == input.getTimestamp()) {
            return input;
        }

        switch (input.getIntervalPeriod()) {
            case "1H", "90MIN" -> input.setLabel(HOUR_LABEL_FORMATTER.format(input.getTimestamp()));
            case "1DAY" -> input.setLabel(input.getTimestamp().atZone(ZoneId.systemDefault())
                    .getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US));
            default -> {
            }
        }
        return input;
    }

}
