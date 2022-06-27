package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutInstructed {

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;
}
