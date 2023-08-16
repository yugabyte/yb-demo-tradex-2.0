package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.repo.ExplainQueryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryAnalysisService {

    @Autowired
    ExplainQueryRepo explainQueryRepo;

    public List<String> analyzeQuery(TradeXDataSourceType dbType, String query, MapSqlParameterSource params) {
        return explainQueryRepo.analyzeQuery(dbType, query, params);
    }

}
