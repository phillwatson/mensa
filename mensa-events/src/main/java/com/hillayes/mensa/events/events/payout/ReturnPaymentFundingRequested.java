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
public class ReturnPaymentFundingRequested {

    @NotNull(message = "Source Account Id is mandatory")
    private UUID sourceAccountId;

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    private UUID payeeId;

    @NotNull(message = "Amount is mandatory")
    @Min(value = 1, message = "Amount must be greater than zero and less than 9,999,999,999")
    @Max(value = 9_999_999_999L, message = "Amount must be greater than zero and less than 9,999,999,999")
    private Long amount;     //Max amount: 99999999.99

    @NotNull(message = "Currency is mandatory")
    @Valid
    private CurrencyType currency;

}
