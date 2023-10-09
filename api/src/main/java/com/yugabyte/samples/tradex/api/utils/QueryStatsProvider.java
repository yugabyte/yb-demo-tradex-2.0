package com.yugabyte.samples.tradex.api.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.QueryAnalysisService;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class QueryStatsProvider {

  final QueryAnalysisService queryAnalysisService;

  public QueryStatsProvider(QueryAnalysisService queryAnalysisService) {
    this.queryAnalysisService = queryAnalysisService;
  }

  public DBOperationResult loadQueryStats(TradeXDataSourceType dbType, Object data,
    boolean analyzeQueries, MapSqlParameterSource params, String query, long timeElapsed,
    ConnectionInfo connectionInfo) {
    List<String> analyzeQuery = Collections.emptyList();

    if (analyzeQueries) {
      analyzeQuery = queryAnalysisService.analyzeQuery(dbType, query, params);
    }

    return new DBOperationResult(data, List.of("Executing ( " + dbType + " ) > " + query,
      QueryParamDisplayUtils.getParameters(params)), analyzeQuery, timeElapsed, connectionInfo);
  }

  public DBOperationResult updateQueryStats(DBOperationResult result, TradeXDataSourceType dbType,
    Boolean inspectQueries, String query, MapSqlParameterSource params, long timeElapsed,
    ConnectionInfo connectionInfo) {

    if (inspectQueries) {
      List<String> analyzeQuery = queryAnalysisService.analyzeQuery(dbType, query, params);
      result.setExplainResults(analyzeQuery);
    }

    result.setConnectionInfo(connectionInfo);
    result.setLatencyMillis(timeElapsed);

    return result;
  }
}
