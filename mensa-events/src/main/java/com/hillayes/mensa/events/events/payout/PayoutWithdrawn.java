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
public class PayoutWithdrawn {

    @Valid
    @NotNull
    private PayoutPayorIds payoutPayorIds;

    @NotNull
    private UUID payoutId;

    @NotNull
    private String reason;
}
