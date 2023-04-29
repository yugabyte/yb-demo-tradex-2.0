package com.yugabyte.samples.tradex.api.domain.business;

import com.yugabyte.samples.tradex.api.domain.db.AppUserId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class SignUpRequest {
    private AppUserId id;
    private String email;
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passkey;
    private Boolean enabled = false;
    private String language;
    private Integer securityPin;
    private PersonalDetails personalDetails;
    private UserNotifications notifications;
    private Integer[] favourites;
}
