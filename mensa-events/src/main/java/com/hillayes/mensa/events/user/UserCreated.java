package com.hillayes.mensa.events.user;

import com.hillayes.mensa.events.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreated implements Event {
    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private Instant dateCreated;
}
