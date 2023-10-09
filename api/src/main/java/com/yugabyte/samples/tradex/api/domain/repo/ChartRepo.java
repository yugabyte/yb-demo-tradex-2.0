package com.yugabyte.samples.tradex.api.domain.repo;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.PortfolioPerfChartEntry;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.utils.Sql;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ChartRepo {
    @Autowired
    TradeXJdbcTemplateResolver jdbcTemplateResolver;

    public Double fetchPortfolioTotalAmount(TradeXDataSourceType dbType, AppUserId appUserId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", appUserId.getId());
        params.addValue("prefRegion", appUserId.getPreferredRegion());

        Double total = jdbcTemplateResolver.resolve(dbType).queryForObject(Sql.Charts.PORTFOLIO_TOTAL, params, Double.class);
        return total;
    }

    public List<PortfolioPerfChartEntry> fetchPortfolioChartValues(TradeXDataSourceType dbType, AppUserId appUserId) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", appUserId.getId());
        params.addValue("prefRegion", appUserId.getPreferredRegion());

        return jdbcTemplateResolver.resolve(dbType).query(Sql.Charts.PORTFOLIO_CHART_SQL,
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
