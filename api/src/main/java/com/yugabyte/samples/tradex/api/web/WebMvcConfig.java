package com.yugabyte.samples.tradex.api.web;


import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDbResolvingInterceptor;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${app.cors.max_age}")
  private final Long maxAge = 3600L;
//  @Value("${app.cors.allowed_origins}")
//  private String[] allowedOrigins;

  private final TradeXDBTypeContext tradeXDBTypeContext;

  public WebMvcConfig(TradeXDBTypeContext tradeXDBTypeContext) {
    this.tradeXDBTypeContext = tradeXDBTypeContext;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    //  log.info("Using allowedOrigin: {}", Arrays.toString(allowedOrigins));
    registry.addMapping("/**")
//            .allowedOrigins(allowedOrigins)
      .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
      .maxAge(maxAge);
  }


  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.ENGLISH);
    return localeResolver;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    return localeChangeInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new TradeXDbResolvingInterceptor(tradeXDBTypeContext));
    registry.addInterceptor(localeChangeInterceptor());
  }

}
