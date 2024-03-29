package com.hillayes.mensa.user.events;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.user.UserCreated;
import com.hillayes.mensa.events.events.user.UserDeclined;
import com.hillayes.mensa.events.events.user.UserDeleted;
import com.hillayes.mensa.events.events.user.UserOnboarded;
import com.hillayes.mensa.events.events.user.UserUpdated;
import com.hillayes.mensa.outbox.sender.EventSender;
import com.hillayes.mensa.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserEventSender {
    private final EventSender eventSender;

    public void sendUserCreated(User user) {
        log.debug("Sending UserCreated event [username: {}]", user.getUsername());

        UserCreated event = UserCreated.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .dateCreated(user.getDateCreated())
                .build();
        eventSender.send(Topic.USER_CREATED, event);
    }

    public void sendUserDeclined(User user) {
        log.debug("Sending UserDeclined event [username: {}]", user.getUsername());

        UserDeclined event = UserDeclined.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .dateCreated(user.getDateCreated())
                .build();
        eventSender.send(Topic.USER_DECLINED, event);
    }

    public void sendUserOnboarded(User user) {
        log.debug("Sending UserOnboarded event [username: {}]", user.getUsername());
        eventSender.send(Topic.USER_ONBOARDED, UserOnboarded.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .dateOnboarded(user.getDateOnboarded())
            .build());
    }

    public void sendUserUpdated(User user) {
        log.debug("Sending UserUpdated event [username: {}]", user.getUsername());
        eventSender.send(Topic.USER_UPDATED, UserUpdated.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .dateOnboarded(user.getDateOnboarded())
            .build());
    }

    public void sendUserDeleted(User user) {
        log.debug("Sending UserDeleted event [username: {}]", user.getUsername());
        eventSender.send(Topic.USER_DELETED, UserDeleted.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .dateCreated(user.getDateCreated())
            .dateOnboarded(user.getDateOnboarded())
            .build());
    }
}
