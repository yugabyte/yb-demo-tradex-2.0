package com.yugabyte.samples.tradex.api.web.utils;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class TradeXDbResolvingInterceptor implements HandlerInterceptor {

    private static final String TRADEX_DB_TYPE_HEADER = "X-TRADEX-DB-TYPE";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        log.trace("Request: {}", request.getRequestURI());

        if (request.getRequestURI().startsWith("/api")) {
            String dbTypeSelected = request.getHeader(TRADEX_DB_TYPE_HEADER);

            if (StringUtils.isEmpty(dbTypeSelected)) {
                log.debug("Missing Request Header, defaulting to single");
                TradeXDBTypeContext.setDBType(TradeXDataSourceType.SINGLE_REGION_MULTI_ZONE);
            } else {
                log.debug("Using DBType: {}", dbTypeSelected);
                TradeXDBTypeContext.setDBType(TradeXDataSourceType.valueOf(dbTypeSelected));
            }
        }
        return true;
    }
}
