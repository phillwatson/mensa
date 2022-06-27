package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayoutFundsRequest {

    @NotNull
    private UUID payoutId;

    @NotNull
    private UUID sourceAccountId;

    @NotNull
    @Min(1)
    private Long amount;

    @NotNull
    private UUID batchId;
}
