package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class BaseController {
    @Autowired
    UserService userService;
    @Autowired
    ConnectionInfoRepo connectionInfoRepo;

    public AppUser fetchUser(Authentication authentication) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(dbType, userDetails.getUsername())
                .orElseThrow( () -> new RuntimeException("User not found"));
    }

    public ConnectionInfo fetchConnectionInfo(String region) {
        return connectionInfoRepo.fetchConnectionDetails(TradeXDBTypeContext.getDbType(), region);
    }
}
