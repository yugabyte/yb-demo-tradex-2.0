package com.yugabyte.samples.tradex.api.domain.db;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat
public enum NotificationType {
    ENABLED,
    DISABLED
}
