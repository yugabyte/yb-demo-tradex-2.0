package com.yugabyte.samples.tradex.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.UserNotifications;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.domain.repo.AppUserRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.utils.AppUserParamUtils;
import com.yugabyte.samples.tradex.api.utils.QueryParamDisplayUtils;
import com.yugabyte.samples.tradex.api.utils.Sql.User;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService implements UserDetailsService {


  private final ConnectionInfoRepo connectionInfoRepo;
  private final AppUserRepo appUserRepo;
  private final AppUserParamUtils paramUtils;

  private final ObjectMapper applicationObjectMapper;

  final TradeXDBTypeContext tradeXDBTypeContext;

  public UserService(ConnectionInfoRepo connectionInfoRepo, AppUserRepo appUserRepo,
    AppUserParamUtils paramUtils, @Qualifier("applicationObjectMapper") ObjectMapper applicationObjectMapper, TradeXDBTypeContext tradeXDBTypeContext) {
    this.connectionInfoRepo = connectionInfoRepo;
    this.appUserRepo = appUserRepo;
    this.paramUtils = paramUtils;
    this.applicationObjectMapper = applicationObjectMapper;
    this.tradeXDBTypeContext = tradeXDBTypeContext;
  }

  @Transactional(readOnly = true)
  public Optional<AppUser> findByEmail(TradeXDataSourceType dbType, String email) {
    return Optional.ofNullable(appUserRepo.findByEmailId(dbType, email));
  }

  @Transactional(readOnly = true)
  public Optional<AppUser> findByAppUserId(TradeXDataSourceType dbType, AppUserId id) {
    return Optional.of(appUserRepo.findByAppUserId(dbType, id));
  }

  @Transactional(readOnly = true)
  public Boolean verifyPin(TradeXDataSourceType dbType, Integer userId, String prefRegion,
    Integer pin) {
    return appUserRepo.verifyPin(dbType, new AppUserId(userId, prefRegion), pin);
  }

  @Transactional(readOnly = true)
  public Boolean existsByEmail(TradeXDataSourceType dbType, String email) {
    return appUserRepo.userExists(dbType, email);
  }

  @Transactional()
  public DBOperationResult createNewUser(TradeXDataSourceType dbType, AppUser user,
    String prefRegion) {

    Instant start = Instant.now();
    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, prefRegion);

    AppUserId createdId = appUserRepo.createNewUser(dbType, user, prefRegion);
    long queryTime = Duration.between(start, Instant.now())
      .toMillis();
    MapSqlParameterSource mapSqlParameterSource = paramUtils.getSQLParams(user, prefRegion);

    return new DBOperationResult(createdId,
      List.of("Executing ( " + dbType + " ) > " + User.INSERT_APP_USER,
        QueryParamDisplayUtils.getParameters(mapSqlParameterSource)), null, queryTime,
      connectionInfo);
  }

  @Transactional()
  public DBOperationResult updateUser(TradeXDataSourceType dbType, AppUser modifiedUser) {

    Instant start = Instant.now();
    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType,
      modifiedUser.getId()
        .getPreferredRegion());

    int rowUpdated = appUserRepo.updateAppUser(dbType, modifiedUser);
    log.info("updated app user");
    MapSqlParameterSource params = paramUtils.getSQLParams(modifiedUser, modifiedUser.getId()
      .getPreferredRegion());
    return new DBOperationResult(rowUpdated,
      List.of("Executing ( " + dbType + " ) > " + User.UPDATE_APP_USER,
        QueryParamDisplayUtils.getParameters(params)), null, Duration.between(start, Instant.now())
      .toMillis(), connectionInfo);
  }

  @Transactional()
  public DBOperationResult updateUserFavourites(TradeXDataSourceType dbType, AppUserId appUserId,
    Integer[] favorites) {

    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType,
      appUserId.getPreferredRegion());
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("prefRegion", appUserId.getPreferredRegion());
    params.addValue("uid", appUserId.getId());
    params.addValue("favourites", favorites);
    Instant start = Instant.now();
    int rowUpdated = appUserRepo.modifyUserFavourites(dbType, appUserId, favorites);

    long queryTime = Duration.between(start, Instant.now())
      .toMillis();

    return new DBOperationResult(rowUpdated,
      List.of("Executing ( " + dbType + " ) > " + User.UPDATE_USER_FAV,
        QueryParamDisplayUtils.getParameters(params)), null, queryTime, connectionInfo);
  }

  @Transactional()
  public DBOperationResult updateUserNotifications(TradeXDataSourceType dbType, AppUserId appUserId,
    UserNotifications notifications) {

    try {
      ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType,
        appUserId.getPreferredRegion());
      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("prefRegion", appUserId.getPreferredRegion());
      params.addValue("uid", appUserId.getId());
      params.addValue("pNotif", applicationObjectMapper.writeValueAsString(notifications));
      Instant start = Instant.now();
      int rowUpdated = appUserRepo.updateNotifications(dbType, appUserId, notifications);

      long queryTime = Duration.between(start, Instant.now())
        .toMillis();

      return new DBOperationResult(rowUpdated,
        List.of("Executing ( " + dbType + " ) > " + User.UPDATE_USER_NOTIF,
          QueryParamDisplayUtils.getParameters(params)), null, queryTime, connectionInfo);
    } catch (JsonProcessingException e) {
      log.error("Caught error while parsing json: {}", notifications);
      throw new RuntimeException(e.getMessage());
    }
  }

  @Transactional()
  public DBOperationResult updateUserLanguage(TradeXDataSourceType dbType, AppUserId appUserId,
    String langCode) {

    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType,
      appUserId.getPreferredRegion());
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("prefRegion", appUserId.getPreferredRegion());
    params.addValue("uid", appUserId.getId());
    params.addValue("langCode", langCode);
    Instant start = Instant.now();
    int rowUpdated = appUserRepo.updateLanguage(dbType, appUserId, langCode);

    long queryTime = Duration.between(start, Instant.now())
      .toMillis();

    return new DBOperationResult(rowUpdated,
      List.of("Executing ( " + dbType + " ) > " + User.UPDATE_USER_LANG,
        QueryParamDisplayUtils.getParameters(params)), null, queryTime, connectionInfo);

  }


  @Transactional()
  public DBOperationResult updatePasskey(TradeXDataSourceType dbType, String email,
    String newPassKey, String prefRegion) {

    ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, prefRegion);
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("uemail", email);
    params.addValue("passKey", newPassKey);
    Instant start = Instant.now();
    int rowUpdated = appUserRepo.updatePassword(dbType, email, newPassKey);

    long queryTime = Duration.between(start, Instant.now())
      .toMillis();

    return new DBOperationResult(rowUpdated,
      List.of("Executing ( " + dbType + " ) > " + User.UPDATE_APP_USER_PWD,
        QueryParamDisplayUtils.getParameters(params)), null, queryTime, connectionInfo);

  }

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUser = appUserRepo.findByEmailId(tradeXDBTypeContext.getDbType(), username);
    return new org.springframework.security.core.userdetails.User(appUser.getEmail(),
      appUser.getPasskey(), appUser.getEnabled(), false, false, false, Collections.emptyList());
  }

}
