package com.hillayes.mensa.payment.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Batch {
    @EqualsAndHashCode.Include
    private UUID batchId; // primary key
    private UUID payoutId; // foreign key to payout.payoutId
    private UUID sourceAccountId; // foreign key to SourceAccountInfo.sourceAccountId
    private Long amount;
    private String sourceCurrency;
    private BatchStatus status;
    private Long statusLastUpdateTimestamp;
    private BatchFundingStatus fundingStatus;
    private Long fundingStatusLastUpdateTimestamp;
    private Integer totalPaymentsInBatch;
    private Integer totalAcceptedPayments;
    private Integer totalRejectedPayments;
    private Long version;
}
