package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutBatchFundsReturnRequest {

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    @NotNull(message = "Batch Id is mandatory")
    private UUID batchId; // velo batch id

    // amount in minor units
    @NotNull(message = "Amount is mandatory")
    @Min(value = 1, message = "Amount must be at least 1")
    private Long amount;

    @NotNull(message = "Source account id is mandatory")
    private UUID sourceAccountId;

    @NotNull(message = "Currency is mandatory")
    @Valid
    private CurrencyType currency;
}
