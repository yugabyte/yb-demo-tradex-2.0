package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class TradeXDbResolvingInterceptor implements HandlerInterceptor {


    private static final String TRADEX_DB_TYPE_HEADER = "X-TRADEX-DB-TYPE";

  public TradeXDbResolvingInterceptor(
    TradeXDBTypeContext tradeXDBTypeContext) {
    this.tradeXDBTypeContext = tradeXDBTypeContext;
  }

  private final TradeXDBTypeContext tradeXDBTypeContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        log.trace("Request: {}", request.getRequestURI());

        if (request.getRequestURI().startsWith("/api")) {
            String dbTypeSelected = request.getHeader(TRADEX_DB_TYPE_HEADER);

            if (StringUtils.isNotEmpty(dbTypeSelected)) {
                log.debug("Using DBType: {}", dbTypeSelected);
                tradeXDBTypeContext.setDbType(TradeXDataSourceType.valueOf(dbTypeSelected));
            }
        }
        return true;
    }
}
