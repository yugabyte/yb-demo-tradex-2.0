package com.yugabyte.samples.tradex.api.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.Portfolio;
import com.yugabyte.samples.tradex.api.domain.business.UserNotifications;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.repo.ConnectionInfoRepo;
import com.yugabyte.samples.tradex.api.domain.repo.ExplainQueryRepo;
import com.yugabyte.samples.tradex.api.service.ApplicationServiceException;
import com.yugabyte.samples.tradex.api.service.DBOperationResult;
import com.yugabyte.samples.tradex.api.service.PortfolioService;
import com.yugabyte.samples.tradex.api.service.UserService;
import com.yugabyte.samples.tradex.api.utils.AppUserParamUtils;
import com.yugabyte.samples.tradex.api.utils.QueryParamDisplayUtils;
import com.yugabyte.samples.tradex.api.utils.SqlProvider;
import com.yugabyte.samples.tradex.api.utils.SqlQueries.UserSql;
import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import com.yugabyte.samples.tradex.api.web.dto.VerifyPinRequest;
import com.yugabyte.samples.tradex.api.web.utils.TradeXDBTypeContext;
import com.yugabyte.samples.tradex.api.web.utils.WebConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.yugabyte.samples.tradex.api.utils.SqlQueries.UserSql.*;

@RestController
@Slf4j
@CrossOrigin
public class UserProfileController {

    @Autowired
    UserService userService;

    @Autowired
    PortfolioService portfolioService;

    @Autowired
    ExplainQueryRepo explainQueryRepo;

    @Autowired
    ConnectionInfoRepo connectionInfoRepo;

    @Autowired
    AppUserParamUtils appUserParamhelper;

    @Autowired
    SqlProvider sqlProvider;

    @Autowired
    AppUserParamUtils paramUtils;

    ObjectMapper mapper = new ObjectMapper();


    @GetMapping("/api/me")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult getUser(Authentication authentication,
                                     @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                             required = false, defaultValue = "false") Boolean inspectQueries) throws ApplicationServiceException {
        Instant start = Instant.now();
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser user = userService.findByEmail(dbType, userDetails.getUsername())
                .orElseThrow(() -> new ApplicationServiceException("User details are missing"));
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, user.getId().getPreferredRegion());

        Map parameters = Map.of("pEmail", userDetails.getUsername());
        List<String> analyzeQuery = Collections.emptyList();
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getUserSQL(FIND_BY_EMAIL_SQL), (Map<String, Object>) parameters);
        }
        return new DBOperationResult(user, List.of("Executing ( " + dbType + " ) > "
                + sqlProvider.getUserSQL(FIND_BY_EMAIL_SQL), parameters.toString()), analyzeQuery,
                Duration.between(start, Instant.now()).toMillis(),
                connectionInfo
        );

    }

    @GetMapping("/api/portfolio")
    @SecurityRequirement(name = "auth-header-bearer")
    public Portfolio getMyPortfolio(@RequestParam(name = "prevId", required = false, defaultValue = "0") int prevId,
                                    @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
                                    Authentication authentication) {

        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            return portfolioService.getPortfolio(TradeXDBTypeContext.getDbType(), userDetails.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/api/user/pin/verify")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult verifyPin(@RequestBody VerifyPinRequest verifyPinRequest,
                                       Authentication authentication,
                                       @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                               required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        AppUser appUserFromDB = userService.findByEmail(dbType, userDetails.getUsername()).get();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, appUserFromDB.getId().getPreferredRegion());
        Instant start = Instant.now();

        Boolean data = userService.verifyPin(dbType, appUserFromDB.getId().getId(),
                appUserFromDB.getId().getPreferredRegion(), verifyPinRequest.getPin());
        String insertQuery = sqlProvider.getUserSQL(UserSql.VERIFY_USE_PIN_SQL);
        Map parameters = Map.of("pUserId", appUserFromDB.getId().getId(),
                "pUserPin", verifyPinRequest.getPin(),
                "prefRegion", appUserFromDB.getId().getPreferredRegion());

        List<String> analyzeQuery = Collections.emptyList();
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, insertQuery, (Map<String, Object>) parameters);
        }

        return new DBOperationResult(data, List.of("Executing ( " + dbType + " ) > "
                + insertQuery, parameters.toString()), analyzeQuery,
                Duration.between(start, Instant.now()).toMillis(), connectionInfo);
    }


    @PostMapping("/api/user")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult createNewUser(@RequestBody AppUser appUser, Authentication authentication,
                                           @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                   required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser appUserFromDB = userService.findByEmail(dbType, userDetails.getUsername()).get();
        DBOperationResult result = userService.createNewUser(dbType, appUser, appUserFromDB.getId().getPreferredRegion());
        MapSqlParameterSource params = appUserParamhelper.getSQLParams(appUser, appUserFromDB.getId().getPreferredRegion());

        List<String> analyzeQuery = Collections.emptyList();
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getUserSQL(INSERT_APP_USER), params);
        }


        result.setExplainResults(analyzeQuery);
        return result;
    }

    @PutMapping("/api/user")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult updateUser(@RequestBody AppUser appUser, Authentication authentication,
                                        @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser appUserFromDB = userService.findByEmail(dbType, userDetails.getUsername()).get();

        Instant start = Instant.now();

        DBOperationResult result = userService.updateUser(dbType, appUser);

        List<String> analyzeQuery = Collections.emptyList();

        if (inspectQueries) {
            MapSqlParameterSource params = appUserParamhelper.getSQLParams(appUser, appUserFromDB.getId().getPreferredRegion());
            params.addValue("uid", appUser.getId().getId());
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getUserSQL(UPDATE_APP_USER), params);
        }

        result.setExplainResults(analyzeQuery);
        result.setLatencyMillis(Duration.between(start, Instant.now()).toMillis());
        return result;
    }

    @PutMapping("/api/me/fav/{stockId}")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult updateUserFav(@PathVariable(name = "stockId") Integer stockId,
                                           @RequestParam(name = "action", required = false, defaultValue = "add") String action,
                                           Authentication authentication,
                                           @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                   required = false, defaultValue = "false") Boolean inspectQueries) {

        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        AppUser appUser = userService.findByEmail(dbType, userDetails.getUsername()).get();
        ConnectionInfo connectionInfo = connectionInfoRepo.fetchConnectionDetails(dbType, appUser.getId().getPreferredRegion());
        Instant start = Instant.now();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("uid", appUser.getId().getId());
        params.addValue("prefRegion", appUser.getId().getPreferredRegion());

        List<String> analyzeQuery = Collections.emptyList();

        List<Integer> favourites = new ArrayList<>();

        if (null != appUser.getFavourites()) {
            favourites.addAll(Arrays.asList(appUser.getFavourites()));
        }

        if ("ADD".equalsIgnoreCase(action) &&
                (appUser.getFavourites() == null || !favourites.contains(stockId))) {
            favourites.add(stockId);
        }

        if ("DEL".equalsIgnoreCase(action) &&
                (appUser.getFavourites() != null && favourites.contains(stockId))) {
            favourites.remove(stockId);

        }
        params.addValue("favourites", favourites.toArray(new Integer[0]));
        DBOperationResult result = userService.updateUserFavourites(dbType, appUser.getId(), favourites.toArray(new Integer[0]));
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, sqlProvider.getUserSQL(UPDATE_USER_FAV), params);
        }

        result.setExplainResults(analyzeQuery);
        result.setLatencyMillis(Duration.between(start, Instant.now()).toMillis());
        result.setConnectionInfo(connectionInfo);


        return result;

    }

    @PutMapping("/api/me/notifs")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult updateUserNotifications(@RequestBody UserNotifications userNotifications,
                                                     Authentication authentication,
                                                     @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                             required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser appUser = userService.findByEmail(dbType, userDetails.getUsername()).get();

        Instant start = Instant.now();
        DBOperationResult result = userService.updateUserNotifications(dbType, appUser.getId(), userNotifications);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("uid", appUser.getId().getId());
        params.addValue("prefRegion", appUser.getId().getPreferredRegion());

        try {
            params.addValue("pNotif", mapper.writeValueAsString(userNotifications));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse user notification details. {}", e.getMessage());
            throw new IllegalArgumentException("failed to parse either personal details or notifications");
        }

        String query = sqlProvider.getUserSQL(UPDATE_USER_NOTIF);
        List<String> analyzeQuery = Collections.emptyList();
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, query, params);
        }

        result.setQueries(List.of("Executing ( " + dbType + " ) > "
                + query, QueryParamDisplayUtils.getParameters(params)));
        result.setLatencyMillis(Duration.between(start, Instant.now()).toMillis());
        result.setExplainResults(analyzeQuery);
        return result;
    }

    @PutMapping("/api/me/lang/{langCode}")
    @SecurityRequirement(name = "auth-header-bearer")
    public DBOperationResult updateUserLanguage(@PathVariable("langCode") String langCode,
                                                Authentication authentication,
                                                @RequestHeader(value = WebConstants.TRADEX_QUERY_ANALYZE_HEADER,
                                                        required = false, defaultValue = "false") Boolean inspectQueries) {
        TradeXDataSourceType dbType = TradeXDBTypeContext.getDbType();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser appUser = userService.findByEmail(dbType, userDetails.getUsername()).get();

        Instant start = Instant.now();

        DBOperationResult result = userService.updateUserLanguage(dbType, appUser.getId(), langCode);
        long queryTime = Duration.between(start, Instant.now()).toMillis();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("uid", appUser.getId().getId());
        params.addValue("prefRegion", appUser.getId().getPreferredRegion());
        params.addValue("langCode", langCode);

        String query = sqlProvider.getUserSQL(UPDATE_USER_LANG);

        List<String> analyzeQuery = Collections.emptyList();
        if (inspectQueries) {
            analyzeQuery = explainQueryRepo.analyzeQuery(dbType, query, params);
        }

        result.setQueries(List.of("Executing ( " + dbType + " ) > "
                + query, QueryParamDisplayUtils.getParameters(params)));
        result.setLatencyMillis(queryTime);
        result.setExplainResults(analyzeQuery);
        return result;
    }

}
