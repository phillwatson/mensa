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
public class PaymentConfirmed {

    public static final String ACTION = "PAYMENT_CONFIRMED";

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;
    @NotNull(message = "Confirmation Channel is mandatory")
    private ConfirmationChannelType confirmationChannel;
}
