package com.yugabyte.samples.tradex.api.events;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.domain.db.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class NewUserCreatedEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final AppUser appUser, final TradeXDataSourceType type) {
        System.out.println("Publishing custom event. ");
        NewUserCreatedEvent customSpringEvent = new NewUserCreatedEvent(this, type, appUser);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}