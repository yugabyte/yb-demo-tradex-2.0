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
@Profile("MR & yugabyte")
public class MultiRegionDbConfig {

    @Autowired
    HikariConfigProvider hikariConfigProvider;

    @Value("${app.mrmz-db.username}")
    String username;
    @Value("${app.mrmz-db.password}")
    String password;
    @Value("${app.mrmz-db.jdbcUrl}")
    String jdbcUrl;
    @Value("${app.mrmz-db.topology-keys}")
    String topologyKeys;

    @Value("${app.load_balance:true}")
    Boolean loadBalance;

    @Bean
    @Qualifier("MULTI_REGION_DB")
    public DataSource multiRegionDB() {
        HikariConfig config = hikariConfigProvider.getConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(loadBalance ? (jdbcUrl + "?load-balance=true&topology-keys=" + topologyKeys) : jdbcUrl);
        config.setPoolName("mrmz-pool");

      return new HikariDataSource(config);

    }

    @Bean
    @Qualifier("MULTI_REGION_DB_TEMPLATE")
    public NamedParameterJdbcTemplate multiRegionJdbcTemplate(@Qualifier("MULTI_REGION_DB") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
