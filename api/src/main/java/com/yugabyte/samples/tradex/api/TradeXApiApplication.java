package com.yugabyte.samples.tradex.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static java.lang.String.join;

@SpringBootApplication(scanBasePackages = "com.yugabyte.samples.tradex.api", exclude = DataSourceAutoConfiguration.class)
@Slf4j
@OpenAPIDefinition(
        info = @Info(title = "TradeX API", version = "1.0", description = "Trading API")
)
public class TradeXApiApplication {
    public TradeXApiApplication(Environment environment) {
        log.info("Active profiles: [{}]", join(",", environment.getActiveProfiles()));
    }

    public static void main(String[] args) {
        SpringApplication.run(TradeXApiApplication.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

}
