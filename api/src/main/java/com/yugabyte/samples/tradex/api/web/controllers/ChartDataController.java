package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.service.ChartDataService;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@CrossOrigin
public class ChartDataController {
    @Autowired
    ChartDataService chartDataService;
    @Autowired
    UserService userService;

    @GetMapping("/api/charts/portfolio-chart")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult getPortfolioChart(Authentication authentication,
                                               @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                       required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser appUser = userService.findByEmail(dbType, userDetails.getUsername()).get();

        return chartDataService.fetchPortfolioChart(TradeXDBTypeContext.getDbType(), appUser.getId(), inspectQueries);
    }

}
