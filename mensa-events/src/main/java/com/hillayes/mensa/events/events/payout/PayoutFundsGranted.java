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
public class PayoutFundsGranted {

    @NotNull
    private UUID batchId;

    @NotNull
    private UUID payoutId;

    @NotNull
    private UUID sourceAccountId;
}
