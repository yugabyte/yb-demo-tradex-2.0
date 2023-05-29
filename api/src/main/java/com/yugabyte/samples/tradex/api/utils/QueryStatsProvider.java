package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.QueryAnalysisService;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class QueryStatsProvider {

    @Autowired
    SqlProvider sqlProvider;

    @Autowired
    QueryAnalysisService queryAnalysisService;


    public DBOperationResult loadChartQueryStats(TradeXDataSourceType dbType,
                                                 Object data, boolean analyzeQueries,
                                                 MapSqlParameterSource params, SqlQueries.ChartSql sqlKey,
                                                 long timeElapsed, ConnectionInfo connectionInfo) {
        String query = sqlProvider.getChartSQL(sqlKey);
        return getDbOperationResult(dbType, data, analyzeQueries, params, timeElapsed, connectionInfo, query);
    }

    public DBOperationResult loadTradeQueryStats(TradeXDataSourceType dbType,
                                                 Object data, boolean analyzeQueries,
                                                 MapSqlParameterSource params, SqlQueries.TradeSql sqlKey,
                                                 long timeElapsed, ConnectionInfo connectionInfo) {
        String query = sqlProvider.getTradeSQL(sqlKey);
        return getDbOperationResult(dbType, data, analyzeQueries, params, timeElapsed, connectionInfo, query);
    }

    public DBOperationResult loadUserQueryStats(TradeXDataSourceType dbType,
                                                Object data, boolean analyzeQueries,
                                                MapSqlParameterSource params, SqlQueries.UserSql sqlKey,
                                                long timeElapsed, ConnectionInfo connectionInfo) {
        String query = sqlProvider.getUserSQL(sqlKey);
        return getDbOperationResult(dbType, data, analyzeQueries, params, timeElapsed, connectionInfo, query);
    }

    public DBOperationResult loadStockQueryStats(TradeXDataSourceType dbType,
                                                 Object data, boolean analyzeQueries,
                                                 MapSqlParameterSource params, SqlQueries.StockSql sqlKey,
                                                 long timeElapsed, ConnectionInfo connectionInfo) {
        String query = sqlProvider.getStockSQL(sqlKey);
        return getDbOperationResult(dbType, data, analyzeQueries, params, timeElapsed, connectionInfo, query);
    }

    private DBOperationResult getDbOperationResult(TradeXDataSourceType dbType, Object data, boolean analyzeQueries,
                                                   MapSqlParameterSource params, long timeElapsed,
                                                   ConnectionInfo connectionInfo, String query) {
        List<String> analyzeQuery = Collections.emptyList();

        if (analyzeQueries) {
            analyzeQuery = queryAnalysisService.analyzeQuery(dbType, query, params);
        }

        return new DBOperationResult(data,
                List.of("Executing ( " + dbType + " ) > " + query,
                        QueryParamDisplayUtils.getParameters(params)),
                analyzeQuery, timeElapsed, connectionInfo);
    }

    public DBOperationResult updateQueryStats(DBOperationResult result, TradeXDataSourceType dbType,
                                              Boolean inspectQueries, SqlQueries.UserSql sqlKey,
                                              MapSqlParameterSource params, long timeElapsed,
                                              ConnectionInfo connectionInfo) {

        if (inspectQueries) {
            String query = sqlProvider.getUserSQL(sqlKey);
            List<String> analyzeQuery = queryAnalysisService.analyzeQuery(dbType, query, params);
            result.setExplainResults(analyzeQuery);
        }

        result.setConnectionInfo(connectionInfo);
        result.setLatencyMillis(timeElapsed);

        return result;
    }
}
