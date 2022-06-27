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
public class ReturnCancelled {

    @NotNull(message = "payoutId is mandatory")
    private UUID payoutId;

    @NotNull(message = "paymentId is mandatory")
    private UUID paymentId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    private UUID payeeId;

    @NotNull(message = "sourceAccountId is mandatory")
    private UUID sourceAccountId;

    @NotNull(message = "railsId is mandatory")
    private String railsId;

    @Min(value = 1)
    @Max(value = 9_999_999_999L)
    private Long amount;

    @NotNull(message = "currency is mandatory")
    private String currency;

    private String reason;
}
