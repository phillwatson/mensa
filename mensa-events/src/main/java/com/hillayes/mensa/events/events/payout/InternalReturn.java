package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalReturn {
    @NotNull
    private UUID paymentId;

    @NotNull
    private UUID payoutId;

    @NotNull
    private String railsId;

    @NotNull
    private UUID sourceAccountId;

    @Valid
    @NotNull
    private PayoutPayorIds payoutPayorIds;

    @Min(value = 1)
    @Max(value = 9_999_999_999L)
    private Long amount;

    @NotNull
    private String currency;

    @NotNull
    private String reason;
}
