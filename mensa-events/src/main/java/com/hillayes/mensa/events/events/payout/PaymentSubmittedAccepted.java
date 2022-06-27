package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubmittedAccepted implements PaymentSubmittedResult {
    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    private UUID payeeId;

    private String railsPaymentId;

    private String railsBatchId;

    private TransmissionType transmissionType;

}
