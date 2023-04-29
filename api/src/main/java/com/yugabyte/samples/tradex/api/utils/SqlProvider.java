package com.yugabyte.samples.tradex.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Properties;

@Component
public class SqlProvider {

    @Autowired
    @Qualifier("USER_SQL")
    Properties userSqlProps;

    @Autowired
    @Qualifier("TRADES_SQL")
    Properties tradeSqlProps;

    @Autowired
    @Qualifier("CHART_SQL")
    Properties chartSqlProps;

    @Autowired
    @Qualifier("STOCK_SQL")
    Properties stockSqlProps;

    @Autowired
    @Qualifier("REFDATA_SQL")
    Properties refdataSqlProps;


    public String getUserSQL(SqlQueries.UserSql key) {
        return Objects.requireNonNull(userSqlProps.getProperty(key.name()));
    }

    public String getTradeSQL(SqlQueries.TradeSql key) {
        return Objects.requireNonNull(tradeSqlProps.getProperty(key.name()));
    }

    public String getChartSQL(SqlQueries.ChartSql key) {
        return Objects.requireNonNull(chartSqlProps.getProperty(key.name()));
    }

    public String getStockSQL(SqlQueries.StockSql key) {
        return Objects.requireNonNull(stockSqlProps.getProperty(key.name()));
    }

    public String getRefSQL(SqlQueries.RefDataSql key) {
        return Objects.requireNonNull(refdataSqlProps.getProperty(key.name()));
    }
}
