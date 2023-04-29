package com.yugabyte.samples.tradex.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile("GEO")
public class GeoPartitionDbConfig {

    @Value("${app.geo-db.username}")
    String username;
    @Value("${app.geo-db.password}")
    String password;
    @Value("${app.geo-db.jdbcUrl}")
    String jdbcUrl;
    @Value("${app.geo-db.topology-keys}")
    String topologyKeys;
    @Value("${app.load_balance:true}")
    Boolean loadBalance;
    @Autowired
    private HikariConfigProvider hikariConfigProvider;

    @Bean
    @Qualifier("GEO_PARTITIONED_DB")
    public DataSource geoDBDataSource() {
        HikariConfig geoHikariConfig = hikariConfigProvider.getConfig();
        geoHikariConfig.setUsername(username);
        geoHikariConfig.setPassword(password);
        geoHikariConfig.setJdbcUrl(loadBalance ? (jdbcUrl + "?load-balance=true&topology-keys=" + topologyKeys) : jdbcUrl);
        geoHikariConfig.setPoolName("geo-pool");
        DataSource ybClusterAwareDataSource = new HikariDataSource(geoHikariConfig);
        return ybClusterAwareDataSource;

    }


    @Bean
    @Qualifier("GEO_PARTITIONED_DB_TEMPLATE")
    public NamedParameterJdbcTemplate geoDbJdbcTemplate(@Qualifier("GEO_PARTITIONED_DB") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


}
