package com.hillayes.mensa.auditor.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@MongoEntity(collection = "payout")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payout {
    @EqualsAndHashCode.Include
    @BsonId
    private UUID id; // primary key

    private UUID submittingPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutFromPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutToPayorId; // foreign key to PayorInfo.payorId

    private PayoutType payoutType;
    private PayoutStatus status;
    private Instant statusLastUpdateTimestamp;
    private List<Payment.StatusHistory> statusHistory;

    private Instant submittedDateTime;
    private Instant quotedDateTime;
    private Instant instructedDateTime;
    private Instant withdrawnDateTime;

    private Integer totalPayments;
    private Integer totalAcceptedPayments;
    private Integer totalRejectedPayments;
    private Integer totalIncompletePayments;
    private Integer totalFailedPayments;
    private Integer totalReturnedPayments;
    private Integer totalConfirmedPayments;
    private Integer totalReleasedPayments;
    private Integer totalWithdrawnPayments;

    private String payoutMemo;
    private String submittedPrincipal;
    private String quotedPrincipal;
    private String instructedPrincipal;
    private String withdrawnPrincipal;

    private PayoutSchedule schedule;
    private List<Batch> batches;

    /**
     * A simple tuple to record the date-time at which the given status was recorded.
     */
    @Data
    @AllArgsConstructor
    public static class StatusHistory {
        private PayoutStatus status;
        private Instant timestamp;
    }
}
