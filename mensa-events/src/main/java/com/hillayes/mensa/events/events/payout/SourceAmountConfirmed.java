package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Sent by rails service when a settlement amount is confirmed for a payment
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceAmountConfirmed {

    @NotNull
    private UUID paymentId;

    @NotNull
    @Min(value = 1)
    private Long sourceAmount;

    @NotNull
    private CurrencyType sourceCurrency;

}
