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
@Profile("MRR")
public class MultiRegionReadReplicaDbConfig {

    @Autowired
    HikariConfigProvider hikariConfigProvider;
    @Value("${app.mrrr-db.username}")
    String username;
    @Value("${app.mrrr-db.password}")
    String password;
    @Value("${app.mrrr-db.jdbcUrl}")
    String jdbcUrl;
    @Value("${app.mrrr-db.topology-keys}")
    String topologyKeys;

    @Value("${app.load_balance:true}")
    Boolean loadBalance;

    @Bean
    @Qualifier("MULTI_REGION_READ_REPLICA_DB")
    public HikariDataSource multiRegionReplicaDBDataSource() {
        HikariConfig mrrHikariConfig = hikariConfigProvider.getConfig();
        mrrHikariConfig.setUsername(username);
        mrrHikariConfig.setPassword(password);
        mrrHikariConfig.setJdbcUrl(loadBalance ? (jdbcUrl + "?load-balance=true&topology-keys=" + topologyKeys) : jdbcUrl);
        mrrHikariConfig.setPoolName("mrrr-pool");
        //mrrHikariConfig.setConnectionInitSql("set yb_read_from_followers = true;set session characteristics as transaction read only;");
        mrrHikariConfig.setConnectionInitSql("set yb_read_from_followers = true;");

        return new HikariDataSource(mrrHikariConfig);

    }

    @Bean
    @Qualifier("MULTI_REGION_READ_REPLICA_DB_TEMPLATE")
    public NamedParameterJdbcTemplate multiRegionReplicaTemplate(@Qualifier("MULTI_REGION_READ_REPLICA_DB") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
