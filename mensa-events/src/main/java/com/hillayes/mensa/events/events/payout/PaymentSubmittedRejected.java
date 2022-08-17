package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubmittedRejected implements PaymentSubmittedResult {

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    private UUID payeeId;

    @Size(max = 40, message = "Payor payment Id must not be longer than {max} characters long")
    private String payorPaymentId;

    @Size(min = 1, max = 255, message = "reason must be between {min} and {max} chars")
    private String reasonCode;

    //optional
    private TransmissionType transmissionType;

}
