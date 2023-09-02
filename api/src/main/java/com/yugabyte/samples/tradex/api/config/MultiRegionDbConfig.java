package com.yugabyte.samples.tradex.api.config;

import static java.lang.String.format;

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
        config.setJdbcUrl(jdbcUrl());
        config.setPoolName("mrmz-pool");

      return new HikariDataSource(config);

    }
    private String jdbcUrl(){
        if(loadBalance){
            return format("%1$s%2$sload_balance=true&yb_servers_refresh_interval=5&topology_keys=%3$s",
              jdbcUrl,
              jdbcUrl.contains("?")?"&":"?",
              topologyKeys);
        }else{
            return jdbcUrl;
        }
    }

    @Bean
    @Qualifier("MULTI_REGION_DB_TEMPLATE")
    public NamedParameterJdbcTemplate multiRegionJdbcTemplate(@Qualifier("MULTI_REGION_DB") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
