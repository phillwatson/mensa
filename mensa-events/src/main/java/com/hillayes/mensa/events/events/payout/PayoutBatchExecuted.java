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
public class PayoutBatchExecuted {

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    @NotNull(message = "Batch Id is mandatory")
    private UUID batchId; // velo batch id

    // populated only for orchestrated payments
    private String remoteSystemId;
}
