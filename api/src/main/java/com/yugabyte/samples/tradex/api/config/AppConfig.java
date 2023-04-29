package com.yugabyte.samples.tradex.api.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Properties;

@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class AppConfig {

    @Value("classpath:/user-queries.xml")
    Resource userSqlFile;

    @Value("classpath:/trade-queries.xml")
    Resource tradeSqlFile;
    @Value("classpath:/chart-queries.xml")
    Resource chartSqlFile;
    @Value("classpath:/stocks-queries.xml")
    Resource stockSqlFile;

    @Value("classpath:/refdata-queries.xml")
    Resource refDataSqlFile;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    @Qualifier("USER_SQL")
    public Properties userSqls() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(userSqlFile.getInputStream());
        log.debug("USER SQL Props loaded: {}", properties.stringPropertyNames());
        return properties;
    }

    @Bean
    @Qualifier("TRADES_SQL")
    public Properties tradeSqls() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(tradeSqlFile.getInputStream());
        log.debug("TRADE SQL Props loaded: {}", properties.stringPropertyNames());
        return properties;
    }

    @Bean
    @Qualifier("CHART_SQL")
    public Properties chartSqls() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(chartSqlFile.getInputStream());
        log.debug("CHART SQL Props loaded: {}", properties.stringPropertyNames());
        return properties;
    }

    @Bean
    @Qualifier("STOCK_SQL")
    public Properties stockSqls() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(stockSqlFile.getInputStream());
        log.debug("STOCK SQL Props loaded: {}", properties.stringPropertyNames());
        return properties;
    }

    @Bean
    @Qualifier("REFDATA_SQL")
    public Properties refDataSqls() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(refDataSqlFile.getInputStream());
        log.debug("REF DATA SQL Props loaded: {}", properties.stringPropertyNames());
        return properties;
    }


    @Bean
    public RestTemplate restTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }


}
