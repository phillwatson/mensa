package com.hillayes.mensa.auditor.domain;

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
    private UUID id; // primary key
    private Instant created;
    private Instant lastUpdatedDateTime;

    @NotNull
    private String name;
    private Address address;
    private String dbaName;
    private Long usdTxnValueReportingThreshold;
}
