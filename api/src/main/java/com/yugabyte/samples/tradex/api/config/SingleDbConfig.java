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

//@Configuration
//@Profile("SINGLE & yugabyte")
public class SingleDbConfig {

    @Autowired
    HikariConfigProvider hikariConfigProvider;
    @Value("${app.srmz-db.username}")
    String username;
    @Value("${app.srmz-db.password}")
    String password;
    @Value("${app.srmz-db.jdbcUrl}")
    String jdbcUrl;
    @Value("${app.srmz-db.topology-keys}")
    String topologyKeys;

    @Value("${app.load_balance:true}")
    Boolean loadBalance;

    @Bean
    @Qualifier("SINGLE_DB")
    public DataSource singleDBDataSource() {
        HikariConfig singleConfig = hikariConfigProvider.getConfig();
        singleConfig.setUsername(username);
        singleConfig.setPassword(password);
        singleConfig.setJdbcUrl(loadBalance ? (jdbcUrl + "?load-balance=true&topology-keys=" + topologyKeys) : jdbcUrl);
        singleConfig.setPoolName("srmz-pool");

      return new HikariDataSource(singleConfig);

    }

    @Bean
    @Qualifier("SINGLE_DB_TEMPLATE")
    public NamedParameterJdbcTemplate singleDBJdbcTemplate(@Qualifier("SINGLE_DB") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
