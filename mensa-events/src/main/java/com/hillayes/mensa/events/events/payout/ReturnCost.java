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
public class ReturnCost {

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "paymentId is mandatory")
    private UUID paymentId;

    @Min(value = 1)
    @Max(value = 9_999_999_999L)
    private Long sourceAmount;

    @Min(value = 1)
    @Max(value = 9_999_999_999L)
    private Long returnedAmount;

    @NotNull(message = "cost is mandatory")
    private Long cost;

    @NotNull(message = "currency is mandatory")
    private String currency;

    private String description;


}
