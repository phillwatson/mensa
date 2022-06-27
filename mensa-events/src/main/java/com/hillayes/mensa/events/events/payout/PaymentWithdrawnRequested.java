package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWithdrawnRequested {
    @NotNull
    private UUID paymentId;

    @NotNull
    private String reason;

    private String railsId;

    private String remoteSystemId;

    // if the payment was withdrawn automatically at instruct time because it
    // was invalid then this will be populated with the relevant PaymentRejectionCode
    private String autoWithdrawnReasonCode;
}
