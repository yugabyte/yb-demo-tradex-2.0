package com.yugabyte.samples.tradex.api.events;

import com.yugabyte.samples.tradex.api.config.TradeXDataSourceType;
import com.yugabyte.samples.tradex.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Arrays;

@Component
@Slf4j
public class NewUserCreationListener {
    @Autowired
    UserService userService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(NewUserCreatedEvent event) {
        log.info("Received New User Created Event. {}", event);
        Arrays.stream(TradeXDataSourceType.values()).filter(e -> !e.equals(event.getDbType())).forEach(k -> {
            try {
                log.info("about to create use in :{}", k.name());
                userService.createNewUser(k, event.getAppUser(), event.getAppUser().getId().getPreferredRegion());
                log.info("user created in: {}", k.name());
            } catch (Exception e) {
                log.error("failed to create user in : {}", k);
            }

        });
        log.info("New User has been replicated to other databases");
    }

}
