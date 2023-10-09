package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.repo.ExplainQueryRepo;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

@Service
public class QueryAnalysisService {

  private final ExplainQueryRepo explainQueryRepo;

  public QueryAnalysisService(ExplainQueryRepo explainQueryRepo) {
    this.explainQueryRepo = explainQueryRepo;
  }

  public List<String> analyzeQuery(TradeXDataSourceType dbType, String query,
    MapSqlParameterSource params) {
    return explainQueryRepo.analyzeQuery(dbType, query, params);
  }

}
