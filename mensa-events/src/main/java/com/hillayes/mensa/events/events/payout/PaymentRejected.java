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
public class PaymentRejected {

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    private UUID payeeId;

    private String remoteId;
    private String remoteSystemId;

    private String paymentMetadata;

    private Long amount;
    private CurrencyType currency;

    private String paymentMemo;

    @Size(max = 40, message = "Payor payment Id must not be longer than {max} characters long")
    private String payorPaymentId;

    @NotNull(message = "Rejection reason code is mandatory")
    @Size(min = 6, message = "Rejection reason code must be at least {min} characters long")
    private String rejectionReasonCode;
}
