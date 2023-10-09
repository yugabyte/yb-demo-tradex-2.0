package com.yugabyte.samples.tradex.api.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.Portfolio;
import com.yugabyte.samples.tradex.api.domain.business.UserNotifications;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.service.ApplicationServiceException;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.PortfolioService;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.utils.AppUserParamUtils;
import com.yugabyte.samples.tradex.api.utils.QueryStatsProvider;
import com.yugabyte.samples.tradex.api.utils.Sql.User;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.dto.VerifyPinRequest;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@CrossOrigin
public class UserProfileController extends BaseController {

  private final PortfolioService portfolioService;
  private final AppUserParamUtils paramUtils;
  private final QueryStatsProvider queryStatsProvider;
  ObjectMapper mapper = new ObjectMapper();

  public UserProfileController(UserService userService, ConnectionInfoRepo connectionInfoRepo,
    TradeXDBTypeContext tradeXDBTypeContext, PortfolioService portfolioService,
    AppUserParamUtils paramUtils, QueryStatsProvider queryStatsProvider) {
    super(userService, connectionInfoRepo, tradeXDBTypeContext);
    this.portfolioService = portfolioService;
    this.paramUtils = paramUtils;
    this.queryStatsProvider = queryStatsProvider;
  }


  @GetMapping("/api/me")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult getUser(Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries)
    throws ApplicationServiceException {
    Instant start = Instant.now();
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser user = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(user.getId()
      .getPreferredRegion());

    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pEmail", user.getEmail());

    return queryStatsProvider.loadQueryStats(dbType, user, inspectQueries, parameters,
      User.FIND_BY_EMAIL_SQL, timeElapsed, connectionInfo);

  }

  @GetMapping("/api/portfolio")
  @SecurityRequirement(name = "auth-header-bearer")
  public Portfolio getMyPortfolio(
    @RequestParam(name = "prevId", required = false, defaultValue = "0") int prevId,
    @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
    Authentication authentication) {

    AppUser user = fetchUser(authentication);
    try {
      return portfolioService.getPortfolio(tradeXDBTypeContext.getDbType(), user.getEmail());
    } catch (Exception e) {
      log.error("Error in processing portfolio requests. Message: {}", e.getMessage());
      log.trace("Error in processing portfolio requests.", e);
      throw e;
    }
  }

  @PostMapping("/api/user/pin/verify")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult verifyPin(@RequestBody VerifyPinRequest verifyPinRequest,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUserFromDB = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUserFromDB.getId()
      .getPreferredRegion());

    Instant start = Instant.now();
    Boolean data = userService.verifyPin(dbType, appUserFromDB.getId()
      .getId(), appUserFromDB.getId()
      .getPreferredRegion(), verifyPinRequest.getPin());
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pUserId", appUserFromDB.getId()
      .getId());
    parameters.addValue("pUserPin", verifyPinRequest.getPin());
    parameters.addValue("prefRegion", appUserFromDB.getId()
      .getPreferredRegion());

    return queryStatsProvider.loadQueryStats(dbType, data, inspectQueries, parameters,
      User.VERIFY_USE_PIN_SQL, timeElapsed, connectionInfo);
  }


  @PostMapping("/api/user")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult createNewUser(@RequestBody AppUser appUser,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUserFromDB = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUserFromDB.getId()
      .getPreferredRegion());

    Instant start = Instant.now();
    DBOperationResult result = userService.createNewUser(dbType, appUser, appUserFromDB.getId()
      .getPreferredRegion());
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();
    MapSqlParameterSource params = paramUtils.getSQLParams(appUser, appUserFromDB.getId()
      .getPreferredRegion());

    return queryStatsProvider.updateQueryStats(result, dbType, inspectQueries, User.INSERT_APP_USER,
      params, timeElapsed, connectionInfo);
  }

  @PutMapping("/api/user")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult updateUser(@RequestBody AppUser appUser, Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUserFromDB = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUserFromDB.getId()
      .getPreferredRegion());

    Instant start = Instant.now();
    DBOperationResult result = userService.updateUser(dbType, appUser);
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    MapSqlParameterSource params = paramUtils.getSQLParams(appUser, appUserFromDB.getId()
      .getPreferredRegion());
    params.addValue("uid", appUser.getId()
      .getId());

    return queryStatsProvider.updateQueryStats(result, dbType, inspectQueries, User.UPDATE_APP_USER,
      params, timeElapsed, connectionInfo);
  }

  @PutMapping("/api/me/fav/{stockId}")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult updateUserFav(@PathVariable(name = "stockId") Integer stockId,
    @RequestParam(name = "action", required = false, defaultValue = "add") String action,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {

    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUser = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUser.getId()
      .getPreferredRegion());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("uid", appUser.getId()
      .getId());
    params.addValue("prefRegion", appUser.getId()
      .getPreferredRegion());

    List<Integer> favourites = new ArrayList<>();

    if (null != appUser.getFavourites()) {
      favourites.addAll(Arrays.asList(appUser.getFavourites()));
    }

    if ("ADD".equalsIgnoreCase(action) && (appUser.getFavourites() == null || !favourites.contains(
      stockId))) {
      favourites.add(stockId);
    }

    if ("DEL".equalsIgnoreCase(action) && (appUser.getFavourites() != null && favourites.contains(
      stockId))) {
      favourites.remove(stockId);
    }

    params.addValue("favourites", favourites.toArray(new Integer[0]));

    Instant start = Instant.now();
    DBOperationResult result = userService.updateUserFavourites(dbType, appUser.getId(),
      favourites.toArray(new Integer[0]));

    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    return queryStatsProvider.updateQueryStats(result, dbType, inspectQueries, User.UPDATE_USER_FAV,
      params, timeElapsed, connectionInfo);
  }

  @PutMapping("/api/me/notifs")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult updateUserNotifications(@RequestBody UserNotifications userNotifications,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUser = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUser.getId()
      .getPreferredRegion());

    Instant start = Instant.now();
    DBOperationResult result = userService.updateUserNotifications(dbType, appUser.getId(),
      userNotifications);
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("uid", appUser.getId()
      .getId());
    params.addValue("prefRegion", appUser.getId()
      .getPreferredRegion());

    try {
      params.addValue("pNotif", mapper.writeValueAsString(userNotifications));
    } catch (JsonProcessingException e) {
      log.error("Failed to parse user notification details. {}", e.getMessage());
      throw new IllegalArgumentException(
        "failed to parse either personal details or notifications");
    }

    return queryStatsProvider.updateQueryStats(result, dbType, inspectQueries,
      User.UPDATE_USER_NOTIF, params, timeElapsed, connectionInfo);
  }

  @PutMapping("/api/me/lang/{langCode}")
  @SecurityRequirement(name = "auth-header-bearer")
  public DBOperationResult updateUserLanguage(@PathVariable("langCode") String langCode,
    Authentication authentication,
    @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER, required = false, defaultValue = "false") Boolean inspectQueries) {
    TradeXDataSourceType dbType = tradeXDBTypeContext.getDbType();

    AppUser appUser = fetchUser(authentication);
    ConnectionInfo connectionInfo = fetchConnectionInfo(appUser.getId()
      .getPreferredRegion());
    Instant start = Instant.now();

    DBOperationResult result = userService.updateUserLanguage(dbType, appUser.getId(), langCode);
    long timeElapsed = Duration.between(start, Instant.now())
      .toMillis();

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("uid", appUser.getId()
      .getId());
    params.addValue("prefRegion", appUser.getId()
      .getPreferredRegion());
    params.addValue("langCode", langCode);

    return queryStatsProvider.updateQueryStats(result, dbType, inspectQueries,
      User.UPDATE_USER_NOTIF, params, timeElapsed, connectionInfo);

  }

}
