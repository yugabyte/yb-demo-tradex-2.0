package com.yugabyte.samples.tradex.api.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class HikariConfigProvider {

    @Value("${TRADEX_DB_POOL_MIN:3}")
    int minPoolSize;

    @Value("${TRADEX_DB_POOL_MAX:5}")
    int maxPoolSize;

    public HikariConfig getConfig() {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(true);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minPoolSize);
        config.setConnectionInitSql("set search_path=public; ");
        config.setConnectionTestQuery("SELECT version();");
        config.setConnectionTimeout(12000);
        config.setKeepaliveTime(12000);
        config.setIdleTimeout(12000);
        Properties poolProps = new Properties();
        poolProps.setProperty("reWriteBatchedInserts", "true");
        poolProps.setProperty("dataSourceClassName", "com.yugabyte.ysql.YBClusterAwareDataSource");
        config.setDataSourceProperties(poolProps);
        return config;
    }

    public HikariConfig getConfigOracle() {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(true);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minPoolSize);
        config.setConnectionTestQuery("SELECT * FROM DUAL");
        config.setConnectionTimeout(12000);
        config.setKeepaliveTime(12000);
        config.setIdleTimeout(12000);
        return config;
    }
}
