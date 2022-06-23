package com.hillayes.mensa.payment.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayorInfo {
    @NotNull
    private UUID payorId; // primary key
    private Instant created;

    @NotNull
    private String name;
    private Address address;
    private Long lastUpdatedDateTime;
    private String dbaName;
    private Long usdTxnValueReportingThreshold;
}
