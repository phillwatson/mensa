package com.hillayes.mensa.events.events.payout;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Holder for all payor ids associated with a Payout
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutPayorIds {

    @NotNull(message = "submittingPayorId is mandatory")
    private UUID submittingPayorId;
    // This will always be set, even on a STANDARD payout
    @NotNull(message = "payoutFromPayorId is mandatory")
    private UUID payoutFromPayorId;
    // This will always be set, even on a STANDARD payout
    @NotNull(message = "payoutToPayorId is mandatory")
    private UUID payoutToPayorId;

}
