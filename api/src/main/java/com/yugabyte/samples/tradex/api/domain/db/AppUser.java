package com.yugabyte.samples.tradex.api.domain.db;

import com.yugabyte.samples.tradex.api.domain.business.PersonalDetails;
import com.yugabyte.samples.tradex.api.domain.business.UserNotifications;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor

public class AppUser extends BaseEntity {

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
