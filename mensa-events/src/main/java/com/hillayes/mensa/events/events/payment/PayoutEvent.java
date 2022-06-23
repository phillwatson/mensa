package com.hillayes.mensa.events.events.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayoutEvent {
    @EqualsAndHashCode.Include
    private UUID payoutId; // primary key
    private UUID submittingPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutFromPayorId; // foreign key to PayorInfo.payorId
    private UUID payoutToPayorId; // foreign key to PayorInfo.payorId
    private String payoutMemo;
    private String status;
    private Instant submittedDateTime;
    private Instant quotedDateTime;
    private Instant instructedDateTime;
    private Instant withdrawnDateTime;
}
