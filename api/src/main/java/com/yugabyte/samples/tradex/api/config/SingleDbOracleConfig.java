package com.yugabyte.samples.tradex.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

//@Configuration
//@Profile("SINGLE & oracle")
public class SingleDbOracleConfig {

    @Autowired
    HikariConfigProvider hikariConfigProvider;
    @Value("${app.orcl-srmz-db.username}")
    String username;
    @Value("${app.orcl-srmz-db.password}")
    String password;
    @Value("${app.orcl-srmz-db.jdbcUrl}")
    String jdbcUrl;

    @Bean
    @Qualifier("SINGLE_DB")
    public DataSource singleDBDataSource() {
        HikariConfig singleConfig = hikariConfigProvider.getConfigOracle();
        singleConfig.setUsername(username);
        singleConfig.setPassword(password);
        singleConfig.setJdbcUrl(jdbcUrl);
        singleConfig.setPoolName("srmz-pool");

      return new HikariDataSource(singleConfig);

    }

    @Bean
    @Qualifier("SINGLE_DB_TEMPLATE")
    public NamedParameterJdbcTemplate singleDBJdbcTemplate(@Qualifier("SINGLE_DB") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
