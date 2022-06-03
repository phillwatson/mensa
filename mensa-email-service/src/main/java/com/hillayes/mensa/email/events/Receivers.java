package com.hillayes.mensa.email.events;

import com.hillayes.mensa.email.service.EmailService;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.receiver.ConcurrentTopicProcessor;
import com.hillayes.mensa.events.receiver.ConsumerFactory;
import com.hillayes.mensa.events.receiver.PeriodicAcknowledgement;
import com.hillayes.mensa.events.receiver.RetryFailureStrategy;
import com.hillayes.mensa.events.receiver.TopicListener;
import com.hillayes.mensa.events.user.UserCreated;
import com.hillayes.mensa.events.user.UserOnboarded;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.time.Duration;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Receivers {
    private final ConsumerFactory consumerFactory;
    private final EmailService emailService;

    void onStart(@Observes StartupEvent ev) {
        log.info("Adding event listeners");

        consumerFactory.addTopicListener(
            new TopicListener(
                Topic.USER_CREATED,
                new ConcurrentTopicProcessor((EventPacket event) -> {
                    UserCreated payload = event.getPayloadContent();
                    log.info("Received UserCreated event [correlationId: {}, timestamp: {}, username: {}]",
                        event.getCorrelationId(), event.getTimestamp(), payload.getUsername());

                    emailService.sendEmail(payload);
                }, 1),
                new PeriodicAcknowledgement(Duration.ofSeconds(10)),
                new RetryFailureStrategy(3)
            )
        );

        consumerFactory.addTopicListener(
            new TopicListener(Topic.USER_ONBOARDED,
                new ConcurrentTopicProcessor((EventPacket event) -> {
                    UserOnboarded payload = event.getPayloadContent();
                    log.info("Received UserOnboarded event [correlationId: {}, timestamp: {}, username: {}]",
                        event.getCorrelationId(), event.getTimestamp(), payload.getUsername());

                    emailService.sendEmail(payload);
                }, 1),
                new PeriodicAcknowledgement(Duration.ofSeconds(10)),
                new RetryFailureStrategy(3)
            )
        );
    }
}
