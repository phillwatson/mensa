package com.hillayes.mensa.events.events.email;

import com.hillayes.mensa.events.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmail implements Event {
    @NotNull
    private String toAddress;

    @NotNull
    private String fromAddress;

    @NotNull
    private String templateId;
}
