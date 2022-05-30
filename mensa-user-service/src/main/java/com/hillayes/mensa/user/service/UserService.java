package com.hillayes.mensa.user.service;

import com.hillayes.mensa.user.auth.PasswordCrypto;
import com.hillayes.mensa.user.domain.User;
import com.hillayes.mensa.user.events.UserEventSender;
import com.hillayes.mensa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BadRequestException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordCrypto passwordCrypto;
    private final UserEventSender userEventSender;

    public User createUser(String username, char[] password, String email) {
        log.info("Creating user [username: {}]", username);
        User user = userRepository.save(User.builder()
            .username(username)
            .passwordHash(passwordCrypto.getHash(password))
            .email(email)
            .build());

        userEventSender.sendUserCreated(user);

        log.info("Created user [username: {}, id: {}]", user.getUsername(), user.getId());
        return user;
    }

    public Optional<User> onboardUser(UUID id) {
        log.info("Onboard user [id: {}]", id);

        return userRepository.findById(id)
            .map(user -> {
                if (user.getDateOnboarded() != null) {
                    throw new BadRequestException("User is already onboard");
                }

                user.setDateOnboarded(Instant.now());
                User result = userRepository.save(user);

                userEventSender.sendUserOnboarded(result);
                log.info("Onboarded user [username: {}, id: {}]", result.getUsername(), result.getId());
                return result;
            });
    }

    public Optional<User> getUser(UUID id) {
        log.info("Retrieving user [id: {}]", id);
        Optional<User> result = userRepository.findById(id);

        log.info("Retrieved user [id: {}, found: {}]", id, result.isPresent());
        return result;
    }
}
