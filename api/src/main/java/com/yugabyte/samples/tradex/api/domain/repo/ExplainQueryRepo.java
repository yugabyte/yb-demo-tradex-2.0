package com.yugabyte.samples.tradex.api.domain.repo;


import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Repository
@Slf4j
@Transactional(propagation = REQUIRES_NEW, readOnly = true)
public class ExplainQueryRepo {

    @Autowired
    TradeXJdbcTemplateResolver resolver;

    public List<String> analyzeQuery(TradeXDataSourceType dbType, String query, Map<String, Object> params) {
        try {
            NamedParameterJdbcTemplate template = resolver.resolve(dbType);
            return template.query("EXPLAIN ANALYZE " + query, params, (rs, rowNum) -> rs.getString(1));
        } catch (DataAccessException e) {
            log.error("Failed to query fetch analyze results. {}", e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }

    public List<String> analyzeQuery(TradeXDataSourceType dbType, String query, MapSqlParameterSource params) {
        try {
            return resolver.resolve(dbType).query("EXPLAIN ANALYZE " + query, params, (rs, rowNum) -> rs.getString(1));
        } catch (DataAccessException e) {
            log.error("Failed to query fetch analyze results. {}", e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }

}
