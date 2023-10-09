package com.yugabyte.samples.tradex.api.web.controllers;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class BaseController {

  public BaseController(
    UserService userService,
    ConnectionInfoRepo connectionInfoRepo,
    TradeXDBTypeContext tradeXDBTypeContext) {
    this.userService = userService;
    this.connectionInfoRepo = connectionInfoRepo;
    this.tradeXDBTypeContext = tradeXDBTypeContext;
  }

  protected UserService userService;
  protected ConnectionInfoRepo connectionInfoRepo;
  protected TradeXDBTypeContext tradeXDBTypeContext;
  public AppUser fetchUser(Authentication authentication) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userService.findByEmail(dbType, userDetails.getUsername())
      .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public ConnectionInfo fetchConnectionInfo(String region) {
    return connectionInfoRepo.fetchConnectionDetails(tradeXDBTypeContext.getDbType(), region);
  }
}
