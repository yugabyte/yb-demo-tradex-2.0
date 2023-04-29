package com.yugabyte.samples.tradex.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppUserParamUtils {

    ObjectMapper mapper = new ObjectMapper();

    public MapSqlParameterSource getSQLParams(AppUser user, String prefRegion) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("prefRegion", prefRegion);
        mapSqlParameterSource.addValue("uemail", user.getEmail());
        mapSqlParameterSource.addValue("upasswd", user.getPasskey());
        mapSqlParameterSource.addValue("uenabled", user.getEnabled());
        mapSqlParameterSource.addValue("ulang", user.getLanguage());

        try {
            mapSqlParameterSource.addValue("upersonaldet", mapper.writeValueAsString(user.getPersonalDetails()));
            mapSqlParameterSource.addValue("unotifications", mapper.writeValueAsString(user.getNotifications()));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse user personal details/notifications. {}", e.getMessage());
            throw new IllegalArgumentException("failed to parse either personal details or notifications");
        }

        if (null == user.getSecurityPin() || user.getSecurityPin() < 0 || user.getSecurityPin() > 9999) {
            throw new IllegalArgumentException("Security Pin should be between 0000 - 9999");
        }
        mapSqlParameterSource.addValue("usecuritypin", user.getSecurityPin());

        log.info("Parameters: {}", mapSqlParameterSource);
        return mapSqlParameterSource;
    }
}
