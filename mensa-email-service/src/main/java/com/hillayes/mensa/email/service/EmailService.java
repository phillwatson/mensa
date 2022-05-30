package com.hillayes.mensa.email.service;

import com.hillayes.mensa.events.user.UserCreated;
import com.hillayes.mensa.events.user.UserOnboarded;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
public class EmailService {
    public void sendEmail(UserCreated userCreated) {
        log.info("Sending user-created email [username: {}, email: {}]",
            userCreated.getUsername(), userCreated.getEmail());
    }

    public void sendEmail(UserOnboarded userOnboarded) {
        log.info("Sending user-onboarded email [username: {}, email: {}]",
            userOnboarded.getUsername(), userOnboarded.getEmail());
    }
}
