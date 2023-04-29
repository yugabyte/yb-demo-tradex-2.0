package com.yugabyte.samples.tradex.api.domain.business;

import com.yugabyte.samples.tradex.api.domain.db.NotificationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserNotifications {
    NotificationType generalNotification = NotificationType.ENABLED;
    NotificationType sound = NotificationType.DISABLED;
    NotificationType vibrate = NotificationType.ENABLED;

    NotificationType appUpdates = NotificationType.DISABLED;
    NotificationType billReminder = NotificationType.DISABLED;
    NotificationType promotion = NotificationType.DISABLED;
    NotificationType discountAvailable = NotificationType.DISABLED;
    NotificationType paymentReminder = NotificationType.DISABLED;

    NotificationType newServiceAvailable = NotificationType.DISABLED;
    NotificationType newTipsAvailable = NotificationType.ENABLED;

}
