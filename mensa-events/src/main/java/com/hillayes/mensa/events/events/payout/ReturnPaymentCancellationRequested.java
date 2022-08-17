package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnPaymentCancellationRequested {

    @NotNull
    private UUID sourceAccountId;

    @NotNull
    private UUID paymentId;

    @NotNull
    private UUID payoutId;

    private UUID payeeId;

    @NotNull
    @Min(value = 1)
    @Max(value = 9_999_999_999L)
    private Long amount;     //Max amount: 99999999.99

}
