package com.hillayes.mensa.user.events;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.UserDeleted;
import com.hillayes.mensa.events.events.UserUpdated;
import com.hillayes.mensa.events.sender.EventSender;
import com.hillayes.mensa.events.events.UserCreated;
import com.hillayes.mensa.events.events.UserOnboarded;
import com.hillayes.mensa.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Future;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserEventSender {
    private final EventSender eventSender;

    public Future<RecordMetadata> sendUserCreated(User user) {
        log.debug("Sending UserCreated event [username: {}]", user.getUsername());
        return eventSender.send(Topic.USER_CREATED, UserCreated.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .build());
    }

    public Future<RecordMetadata> sendUserOnboarded(User user) {
        log.debug("Sending UserOnboarded event [username: {}]", user.getUsername());
        return eventSender.send(Topic.USER_ONBOARDED, UserOnboarded.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .dateOnboarded(user.getDateOnboarded())
            .build());
    }

    public Future<RecordMetadata> sendUserUpdated(User user) {
        log.debug("Sending UserUpdated event [username: {}]", user.getUsername());
        return eventSender.send(Topic.USER_UPDATED, UserUpdated.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .dateOnboarded(user.getDateOnboarded())
            .build());
    }

    public Future<RecordMetadata> sendUserDeleted(User user) {
        log.debug("Sending UserDeleted event [username: {}]", user.getUsername());
        return eventSender.send(Topic.USER_DELETED, UserDeleted.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .dateOnboarded(user.getDateOnboarded())
            .build());
    }
}
