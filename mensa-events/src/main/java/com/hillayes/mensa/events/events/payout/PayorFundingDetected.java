package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayorFundingDetected {

    // Note that this is incorrectly named - it should just be fundingId as not all fundings were the result of a request
    @NotNull(message="Funding Request Id is mandatory")
    private UUID fundingRequestId;
    
    @NotNull(message="Payor Id is mandatory")
    private UUID payorId;

    private String fundingRef;

    @NotNull(message="Amount is mandatory")
    private Long amount;

    @NotNull(message="Currency is mandatory")
    @Valid
    private CurrencyType currency;

    private String physicalAccountName;

    @Size(min=1, max=1024, message="Comment must be between {min} and {max} characters long")
    private String comment;

    /**
     * Indicates whether the funds were detected via AUTOMATIC or MANUAL means
     */
    @NotNull(message="Allocation Type is mandatory")
    private AllocationType allocationType;

    /**
     * Indicated whether the source was a Wire transfer or an completion of an ACH Debit request. Only expected when allocation type is AUTOMATIC
     */
    private SourceType sourceType;
    private String sourceAccountName;
    private UUID sourceAccountId;
    private String additionalInformation; // additional info from the rails - maybe the name of the payer that made the deposit
    private String railsId;
}
