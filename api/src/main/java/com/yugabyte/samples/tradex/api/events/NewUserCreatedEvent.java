package com.yugabyte.samples.tradex.api.events;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import org.springframework.context.ApplicationEvent;

public class NewUserCreatedEvent extends ApplicationEvent {
    TradeXDataSourceType dbType;
    AppUser appUser;

    public NewUserCreatedEvent(Object source, TradeXDataSourceType type, AppUser appUser) {
        super(source);
        this.dbType = type;
        this.appUser = appUser;
    }

    public TradeXDataSourceType getDbType() {
        return dbType;
    }

    public AppUser getAppUser() {
        return appUser;
    }

}
