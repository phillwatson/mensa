package com.hillayes.mensa.email.events;

import com.hillayes.mensa.email.service.EmailService;
import com.hillayes.mensa.events.config.ConsumerBean;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.listeners.ConcurrentTopicProcessor;
import com.hillayes.mensa.events.listeners.PeriodicAcknowledgement;
import com.hillayes.mensa.events.listeners.RetryFailureStrategy;
import com.hillayes.mensa.events.listeners.TopicConsumer;
import com.hillayes.mensa.events.user.UserCreated;
import com.hillayes.mensa.events.user.UserOnboarded;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Properties;

@Singleton
@Slf4j
public class Receivers {
    private final Properties consumerConfig;
    private final EmailService emailService;

    public Receivers(@ConsumerBean Properties consumerConfig,
                     EmailService emailService) {
        this.consumerConfig = consumerConfig;
        this.emailService = emailService;
    }

    private TopicConsumer userCreatedConsumer;
    private TopicConsumer userOnboardedConsumer;

    void onStart(@Observes StartupEvent ev) {
        log.info("Adding event listeners");

        userCreatedConsumer = new TopicConsumer(Topic.USER_CREATED, consumerConfig,
            new PeriodicAcknowledgement(Duration.ofSeconds(10)),
            new RetryFailureStrategy(3),
            new ConcurrentTopicProcessor((EventPacket event) -> {
                log.info("Received UserCreated event [correlationId: {}, timestamp: {}]",
                    event.getCorrelationId(), event.getTimestamp());

                UserCreated payload = event.getPayloadContent();
                log.info("Received UserCreated event [username: {}]", payload.getUsername());

                emailService.sendEmail(payload);
            }, 1));

        userOnboardedConsumer = new TopicConsumer(Topic.USER_ONBOARDED, consumerConfig,
            new PeriodicAcknowledgement(Duration.ofSeconds(10)),
            new RetryFailureStrategy(3),
            new ConcurrentTopicProcessor((EventPacket event) -> {
                log.info("Received UserOnboarded event [correlationId: {}, timestamp: {}]",
                    event.getCorrelationId(), event.getTimestamp());

                UserOnboarded payload = event.getPayloadContent();
                log.info("Received UserOnboarded event [username: {}]", payload.getUsername());

                emailService.sendEmail(payload);
            }, 1));
    }

    void onStop(@Observes ShutdownEvent ev) throws InterruptedException {
        userCreatedConsumer.stop();
        userOnboardedConsumer.stop();
    }
}
