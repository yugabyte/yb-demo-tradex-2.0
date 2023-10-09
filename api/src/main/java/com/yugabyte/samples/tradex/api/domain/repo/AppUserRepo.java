package com.yugabyte.samples.tradex.api.domain.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.business.UserNotifications;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import com.yugabyte.samples.tradex.api.service.AppUserRowMapper;
import com.yugabyte.samples.tradex.api.utils.AppUserParamUtils;
import com.yugabyte.samples.tradex.api.utils.EmailValidator;
import com.yugabyte.samples.tradex.api.utils.Sql.User;
import com.yugabyte.samples.tradex.api.utils.TradeXJdbcTemplateResolver;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AppUserRepo {

    @Autowired
    TradeXJdbcTemplateResolver jdbcTemplateResolver;
    @Autowired
    AppUserParamUtils helper;
    @Autowired
    AppUserParamUtils paramUtils;
    ObjectMapper mapper = new ObjectMapper();

    public AppUser findByEmailId(TradeXDataSourceType dbType, String email) {

        if (StringUtils.isEmpty(email) || !EmailValidator.isValidEmail(email)) {
            log.error("Invalid Email Provided: {}", email);
            throw new IllegalArgumentException("Invalid Email");
        }

        try {
            NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
            AppUser user = template.queryForObject(
              User.FIND_BY_EMAIL_SQL, Map.of("pEmail", email),
                    new AppUserRowMapper());
            return user;
        } catch (DataAccessException e) {
            log.error("No User exists by email id: {}", email);
            throw e;
        }
    }

    public AppUser findByAppUserId(TradeXDataSourceType dbType, AppUserId id) {

        if (null == id || (id.getId() == null || StringUtils.isEmpty(id.getPreferredRegion()))) {
            log.error("Invalid AppUser Id Provided: {}", id);
            throw new IllegalArgumentException("Invalid AppUser id provided");
        }

        try {
            NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
            return template.queryForObject(User.FIND_BY_ID,
                    Map.of("uid", id.getId(), "prefRegion",
                            id.getPreferredRegion()), new AppUserRowMapper());
        } catch (DataAccessException e) {
            log.error("No User exists by id: {}, prefRegion:{}", id.getId(), id.getPreferredRegion());
            throw e;
        }
    }

    public Boolean userExists(TradeXDataSourceType dbType, String email) {

        if (StringUtils.isEmpty(email) || !EmailValidator.isValidEmail(email)) {
            log.error("Invalid Email Provided: {}", email);
            throw new IllegalArgumentException("Invalid Email");
        }

        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        return template.queryForObject(User.EXISTS_BY_EMAIL_SQL, Map.of("pEmail", email),
                (rs, rowNum) -> rs.getBoolean(1));
    }

    public Boolean verifyPin(TradeXDataSourceType dbType, AppUserId appUserId, Integer pin) {

        if (null == appUserId || (appUserId.getId() == null || StringUtils.isEmpty(appUserId.getPreferredRegion()))) {
            log.error("Invalid AppUser Id Provided: {}", appUserId);
            throw new IllegalArgumentException("Invalid AppUser id provided");
        }

        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        return template.queryForObject(User.VERIFY_USE_PIN_SQL,
                Map.of("pUserId", appUserId.getId(),
                        "pUserPin", pin, "prefRegion", appUserId.getPreferredRegion()),
                (rs, rowNum) -> rs.getBoolean(1));
    }

    public int updateAppUser(TradeXDataSourceType dbType, AppUser modifiedUser) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);

        if (userExists(dbType, modifiedUser.getEmail())) {
            MapSqlParameterSource params = paramUtils.getSQLParams(modifiedUser, modifiedUser.getId().getPreferredRegion());
            params.addValue("uid", modifiedUser.getId().getId());
            //params.addValue("passKey", modifiedUser.getPasskey());
            int rowUpdated = template.update(User.UPDATE_APP_USER, params);
            return rowUpdated;
        }

        throw new IllegalArgumentException("User not found");
    }

    public int modifyUserFavourites(TradeXDataSourceType dbType, AppUserId userId, Integer[] favs) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        int rowUpdated = template.update(User.UPDATE_USER_FAV,
                Map.of("uid", userId.getId(), "prefRegion", userId.getPreferredRegion(),
                        "favourites", favs));
        return rowUpdated;
    }

    public AppUserId createNewUser(TradeXDataSourceType dbType, AppUser user, String prefRegion) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        MapSqlParameterSource mapSqlParameterSource = helper.getSQLParams(user, prefRegion);
        KeyHolder holder = new GeneratedKeyHolder();
        template.update(User.INSERT_APP_USER, mapSqlParameterSource,
                holder, new String[]{"ID", "PREFERRED_REGION"});
        AppUserId createdId = new AppUserId((Integer) holder.getKeys().get("id"),
                (String) holder.getKeys().get("preferred_region"));
        log.info("INSERTED KEYHOLDER: {}", holder.getKeys());
        return createdId;
    }

    public int updateNotifications(TradeXDataSourceType dbType, AppUserId userId, UserNotifications notifications) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        int rowUpdated = 0;
        try {
            rowUpdated = template.update(User.UPDATE_USER_NOTIF,
                    Map.of("uid", userId.getId(), "prefRegion", userId.getPreferredRegion(),
                            "pNotif", mapper.writeValueAsString(notifications)));
            return rowUpdated;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public int updateLanguage(TradeXDataSourceType dbType, AppUserId userId, String langCode) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        int rowUpdated = template.update(User.UPDATE_USER_LANG,
                Map.of("uid", userId.getId(), "prefRegion", userId.getPreferredRegion(),
                        "langCode", langCode));
        return rowUpdated;
    }

    public int updatePassword(TradeXDataSourceType dbType, String email, String newPasskey) {
        NamedParameterJdbcTemplate template = jdbcTemplateResolver.resolve(dbType);
        int rowUpdated = template.update(User.UPDATE_APP_USER_PWD,
                Map.of("uemail", email, "passKey", newPasskey));
        return rowUpdated;
    }

}
