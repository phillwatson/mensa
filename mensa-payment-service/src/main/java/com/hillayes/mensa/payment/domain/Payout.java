package com.hillayes.mensa.payment.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payout {
    @EqualsAndHashCode.Include
    private UUID payoutId; // primary key
    private UUID submittingPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutFromPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutToPayorId; // foreign key to PayorInfo.payorId
    private PayoutType payoutType;
    private PayoutStatus status;
    private Long statusLastUpdateTimestamp;
    private Long submittedDateTime;
    private Long quotedDateTime;
    private Long instructedDateTime;
    private Long withdrawnDateTime;
    private Integer totalPayments;
    private Integer totalAcceptedPayments;
    private Integer totalRejectedPayments;
    private Integer totalIncompletePayments;
    private Integer totalFailedPayments;
    private Integer totalReturnedPayments;
    private Integer totalConfirmedPayments;
    private Integer totalReleasedPayments;
    private Integer totalWithdrawnPayments;
    private List<Batch> batches;
    private String payoutMemo;
    private String submittedPrincipal;
    private String quotedPrincipal;
    private String instructedPrincipal;
    private String withdrawnPrincipal;
    private PayoutSchedule schedule;
    private Long version;
}
