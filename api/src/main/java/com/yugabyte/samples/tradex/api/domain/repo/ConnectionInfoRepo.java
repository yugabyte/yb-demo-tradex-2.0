package com.yugabyte.samples.tradex.api.domain.repo;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Repository
@Slf4j
@Transactional(readOnly = true)
public class ConnectionInfoRepo {

    final String FETCH_CONN_INFO_SQL = """
            SELECT host, cloud,zone,region, num_connections,node_type FROM yb_servers() where host = host(inet_server_addr())
            """;
    @Autowired
    TradeXJdbcTemplateResolver jdbcTemplateResolver;

    public ConnectionInfo fetchConnectionDetails(TradeXDataSourceType dbType, String partKey) {

        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        ConnectionInfo connectionInfo = template.queryForObject(FETCH_CONN_INFO_SQL,
                new HashMap(0), (RowMapper<ConnectionInfo>) (rs, rowNum) -> new ConnectionInfo(
                        rs.getString("host"),
                        rs.getString("cloud"),
                        rs.getString("region"),
                        rs.getString("zone"), TradeXDataSourceType.GEO_PARTITIONED.equals(dbType) ? partKey : ""));

        return connectionInfo;
    }
}


