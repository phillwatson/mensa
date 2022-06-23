package com.hillayes.mensa.events.events.payment;

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
public class PaymentEvent {
    @EqualsAndHashCode.Include
    private UUID paymentId; // primary key
    private UUID payoutId; // foreign key to payout.payoutId
    private UUID batchId; // foreign key to batch.batchId
    private UUID payeeId; // foreign key to PayeeInfo.payeeId
    private String memo;
    private String sourceCurrency;
    private Long sourceAmount;
    private String status;
}
