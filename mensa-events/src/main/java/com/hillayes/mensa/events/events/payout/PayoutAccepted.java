package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutAccepted {
    @NotNull
    private UUID payoutId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull
    @Min(1)
    @Max(2000)
    private Integer numberOfAcceptedPayments;

    @NotNull
    @Min(0)
    @Max(2000)
    private Integer numberOfRejectedPayments;

    @NotNull
    @Size(min = 1, max = 2000)
    @Valid
    private List<BatchSummary> batches;
}
