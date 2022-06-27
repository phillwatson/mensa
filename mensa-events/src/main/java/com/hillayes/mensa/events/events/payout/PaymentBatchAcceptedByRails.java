package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBatchAcceptedByRails {

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "payoutId is mandatory")
    private UUID payoutId;

    @NotNull(message = "batchId is mandatory")
    private UUID batchId;

}
