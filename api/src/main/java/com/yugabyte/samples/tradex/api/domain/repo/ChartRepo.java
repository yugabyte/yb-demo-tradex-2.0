package com.yugabyte.samples.tradex.api.domain.repo;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.PortfolioPerfChartEntry;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.utils.SqlProvider;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yugabyte.samples.tradex.api.utils.SqlQueries.ChartSql.PORTFOLIO_CHART_SQL;
import static com.yugabyte.samples.tradex.api.utils.SqlQueries.ChartSql.PORTFOLIO_TOTAL;

@Repository
public class ChartRepo {
    @Autowired
    TradeXJdbcTemplateResolver jdbcTemplateResolver;

    @Autowired
    SqlProvider sqlProvider;

    public Double fetchPortfolioTotalAmount(TradeXDataSourceType dbType, AppUserId appUserId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", appUserId.getId());
        params.addValue("prefRegion", appUserId.getPreferredRegion());

        Double total = jdbcTemplateResolver.resolve(dbType).queryForObject(sqlProvider.getChartSQL(PORTFOLIO_TOTAL), params, Double.class);
        return total;
    }

    public List<PortfolioPerfChartEntry> fetchPortfolioChartValues(TradeXDataSourceType dbType, AppUserId appUserId) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", appUserId.getId());
        params.addValue("prefRegion", appUserId.getPreferredRegion());

        return jdbcTemplateResolver.resolve(dbType).query(sqlProvider.getChartSQL(PORTFOLIO_CHART_SQL),
                params,
                (rs, rowNum) -> {
                    PortfolioPerfChartEntry entry = new PortfolioPerfChartEntry(
                            rs.getString("interval_period"),
                            rs.getDouble("total"),
                            rs.getTimestamp("price_time").toInstant(),
                            rs.getString("interval_period"));

                    return entry;
                });
    }
}
