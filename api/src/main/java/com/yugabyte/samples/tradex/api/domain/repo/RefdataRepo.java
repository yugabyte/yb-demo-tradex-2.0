package com.yugabyte.samples.tradex.api.domain.repo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.DBNode;
import com.yugabyte.samples.tradex.api.domain.db.RefDatum;
import com.yugabyte.samples.tradex.api.domain.db.RefDatumId;
import com.yugabyte.samples.tradex.api.service.ApplicationServiceException;
import com.yugabyte.samples.tradex.api.utils.Sql;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class RefdataRepo {

  private final ObjectMapper applicationObjectMapper;
  private final TradeXJdbcTemplateResolver jdbcTemplateResolver;

  public RefdataRepo(@Qualifier("applicationObjectMapper") ObjectMapper applicationObjectMapper,
    TradeXJdbcTemplateResolver jdbcTemplateResolver) {
    this.applicationObjectMapper = applicationObjectMapper;
    this.jdbcTemplateResolver = jdbcTemplateResolver;
  }

  public <T> List fetchRefDataAsList(TradeXDataSourceType dataSourceType, String keyname,
    Class<T> targetType) throws ApplicationServiceException {
    try {
      NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dataSourceType);
      RefDatum refData = template.queryForObject(Sql.RefData.REFDATA_BY_KEY_SQL,
        Map.of("pKeyName", keyname), (rs, rowNum) -> {
          RefDatum r = new RefDatum();
          r.setId(
            new RefDatumId(rs.getInt("id"), rs.getString("key_name"), rs.getString("classifier")));

          r.setKeyValue(rs.getString("key_value"));
          return r;
        });

      if (null == refData) {
        return Collections.emptyList();
      }

      ObjectNode node = applicationObjectMapper.readValue(refData.getKeyValue(), ObjectNode.class);
      JsonNode locations = node.get(keyname);
      TypeFactory typeFactory = applicationObjectMapper.getTypeFactory();
      return applicationObjectMapper.treeToValue(locations,
        typeFactory.constructCollectionType(List.class, targetType));
    } catch (JsonProcessingException e) {
      log.error("Failed parsing refdata: {}", e.getMessage());
      throw new ApplicationServiceException("Failed to fetch reference data: " + keyname);
    }
  }

  public List<DBNode> fetchDBNodes(TradeXDataSourceType dataSourceType) {
    NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dataSourceType);
    return template.query(Sql.RefData.FETCH_DB_NODES, (rs, rowNum) -> {
      String region = rs.getString("region");

      return new DBNode(UUID.randomUUID()
        .toString(), region, rs.getString("zone"), rs.getString("node_type"), null);
    });
  }

}
