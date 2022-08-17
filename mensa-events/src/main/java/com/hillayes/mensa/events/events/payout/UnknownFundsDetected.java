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
@AllArgsConstructor
@NoArgsConstructor
public class UnknownFundsDetected {

    private String fundingRef;
    
    @NotNull(message = "FundingId is mandatory")
    private UUID fundingId;

    @NotNull(message="Amount is mandatory")
    private Long amount;

    @NotNull(message="Currency is mandatory")
    @Valid
    private CurrencyType currency;

    @NotNull(message="PhysicalAccountName is mandatory")
    private String physicalAccountName;
}
