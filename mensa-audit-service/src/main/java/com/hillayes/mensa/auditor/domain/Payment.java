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

@MongoEntity(collection = "payment")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {
    @EqualsAndHashCode.Include
    @BsonId
    private UUID id; // primary key

    private UUID payoutId; // foreign key to payout.payoutId
    private UUID batchId; // foreign key to batch.batchId

    private UUID submittingPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutFromPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutToPayorId; // foreign key to PayorInfo.payorId

    private UUID payeeId; // foreign key to PayeeInfo.payeeId
    private UUID sourceAccountId; // foreign key to SourceAccountInfo.sourceAccountId

    private PaymentStatus status;
    private List<StatusHistory> statusHistory;

    private String remoteId;
    private String remoteSystemId;

    private String sourceCurrency;
    private Long sourceAmount;

    private String paymentCurrency;
    private Long paymentAmount;

    private Instant submittedTimestamp;
    private Instant acceptedByRailsTimestamp;
    private String routingNumber;
    private String accountNumber;
    private String iban;

    private UUID paymentChannelId;
    private String paymentChannelName;

    private String accountName;
    private String payeeAddressCountryCode;
    private String memo;
    private UUID quoteId;
    private String payorPaymentId;
    private String filenameReference;
    private String individualIdentificationNumber;
    private String traceNumber;

    private String returnReason;
    private String rejectionReason;
    private String withdrawnReason;
    private String autoWithdrawnReasonCode;

    private TransactionType transactionType;

    private String railsId;
    private String railsPaymentId;
    private String railsBatchId;
    private String remoteSystemPaymentId;
    private String paymentTrackingReference;
    private String paymentMetadata;
    private Instant lastEventTimestamp;
    private String eta;
    private String transmissionType;
    private String paymentScheme;

    /**
     * A simple tuple to record the date-time at which the given status was recorded.
     */
    @Data
    @AllArgsConstructor
    public static class StatusHistory {
        private PaymentStatus status;
        private Instant timestamp;
    }
}
