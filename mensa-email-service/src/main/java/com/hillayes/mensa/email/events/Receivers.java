package com.hillayes.mensa.email.events;

import com.hillayes.mensa.email.service.EmailService;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.user.UserCreated;
import com.hillayes.mensa.events.events.user.UserOnboarded;
import io.smallrye.reactive.messaging.annotations.Blocking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Receivers {
    private final EmailService emailService;

    @Incoming("user_created")
    @Blocking
    public void userCreatedTopicListener(EventPacket payload) {
        UserCreated content = payload.getPayloadContent();
        emailService.sendEmail(content);
    }

    @Incoming("user_onboarded")
    @Blocking
    public void userOnboardedTopicListener(EventPacket payload) {
        UserOnboarded content = payload.getPayloadContent();
        emailService.sendEmail(content);
    }
}
