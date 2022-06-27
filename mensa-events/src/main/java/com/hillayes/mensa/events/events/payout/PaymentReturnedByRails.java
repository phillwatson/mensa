package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReturnedByRails {

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    private UUID payeeId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "railsId is mandatory")
    private String railsId;

    @NotNull(message = "Source Account Id is mandatory")
    private UUID sourceAccountId;

    @NotNull(message = "Return Amount is mandatory")
    @Min(value = 1L, message = "Return Amount must be greater than zero and less than 9,999,999,999")
    @Max(value = 9999999999L, message = "Return Amount must be greater than zero and less than 9,999,999,999")
    private Long returnAmount;

    @NotNull(message = "currency is mandatory")
    private CurrencyType currency;

    private String returnReasonCode;

    private String additionalInformation;

    private UUID paymentChannelId;

}
