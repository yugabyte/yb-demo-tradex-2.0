package com.yugabyte.samples.tradex.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.samples.tradex.api.domain.business.PersonalDetails;
import com.yugabyte.samples.tradex.api.domain.business.UserNotifications;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;

@Slf4j
public class AppUserRowMapper implements RowMapper<AppUser> {

  private final ObjectMapper applicationObjectMapper;

  public AppUserRowMapper(@Qualifier("applicationObjectMapper") ObjectMapper applicationObjectMapper) {
    this.applicationObjectMapper = applicationObjectMapper;
  }

  @Override
  public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {

    AppUser user = new AppUser();
    user.setId(new AppUserId(rs.getInt("id"), rs.getString("preferred_region")));
    user.setEmail(rs.getString("email"));
    user.setEnabled(rs.getBoolean("enabled"));
    user.setLanguage(rs.getString("user_language"));
    user.setPasskey(rs.getString("passkey"));

    if (rs.getTimestamp("created_date") != null) {
      user.setCreatedDate(rs.getTimestamp("created_date")
        .toInstant());
    }

    if (rs.getTimestamp("updated_date") != null) {
      user.setUpdatedDate(rs.getTimestamp("updated_date")
        .toInstant());
    }

    try {
      user.setPersonalDetails(
        applicationObjectMapper.readValue(rs.getString("personal_details"), PersonalDetails.class));
      user.setNotifications(applicationObjectMapper.readValue(rs.getString("user_notifications"),
        UserNotifications.class));
    } catch (JsonProcessingException e) {
      log.error("Failed to parse user personal details. {}", e.getMessage());
    }

    Array favArray = rs.getArray("favourites");
    if (favArray != null && favArray.getArray() != null) {
      user.setFavourites((Integer[]) favArray.getArray());
    }

    if (hasColumn(rs, "security_pin")) {
      int securityPin = rs.getInt("security_pin");
      if (securityPin != 0) {
        user.setSecurityPin(securityPin);
      }
    }

    return user;

  }


  public boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columns = rsmd.getColumnCount();
    for (int x = 1; x <= columns; x++) {
      if (columnName.equals(rsmd.getColumnName(x))) {
        return true;
      }
    }
    return false;
  }

}
