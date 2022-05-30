package com.hillayes.mensa.email.events;

import com.hillayes.mensa.email.service.EmailService;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.reciever.ConsumerFactory;
import com.hillayes.mensa.events.user.UserCreated;
import com.hillayes.mensa.events.user.UserOnboarded;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Receivers {
    private final ConsumerFactory consumerFactory;
    private final EmailService emailService;

    void onStart(@Observes StartupEvent ev) {
        log.info("Adding event listeners");

        consumerFactory.addListener(Topic.USER_CREATED, (EventPacket event) -> {
            log.info("Received UserCreated event [correlationId: {}, timestamp: {}]",
                event.getCorrelationId(), event.getTimestamp());

            UserCreated payload = event.getPayloadContent();
            log.info("Received UserCreated event [username: {}]", payload.getUsername());

            emailService.sendEmail(payload);
        });

        consumerFactory.addListener(Topic.USER_ONBOARDED, (EventPacket event) -> {
            log.info("Received UserOnboarded event [correlationId: {}, timestamp: {}]",
                event.getCorrelationId(), event.getTimestamp());

            UserOnboarded payload = event.getPayloadContent();
            log.info("Received UserOnboarded event [username: {}]", payload.getUsername());

            emailService.sendEmail(payload);
        });
    }
}
