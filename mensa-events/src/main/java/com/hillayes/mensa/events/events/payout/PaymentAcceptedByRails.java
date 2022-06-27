package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAcceptedByRails {

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    // The Estimated Time of Arrival of the payment i.e. an estimate of how long it will take to hit the payee's bank account
    // Jackson note: For JSON representation, we will use an ISO 8601 Duration string - see https://en.wikipedia.org/wiki/ISO_8601#Durations
    //               e.g. "eta":"PT72H"
    //               - This is honoured by the Velo MarshallingUtil ObjectMapper as it is configured with the module JSR310Module
    //               - It's also honoured by Spring-generated ObjectMapper as it is auto configured with the module JavaTimeModule, which is the new name for JSR310Module
    //                 Auto config is done by Spring class: org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.registerWellKnownModulesIfAvailable
    @NotNull(message = "ETA is mandatory")
    private Duration eta;

    private String remoteSystemId;
}
