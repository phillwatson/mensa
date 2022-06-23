package com.hillayes.mensa.auditor.domain;

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
public class SourceAccountInfo {
    @EqualsAndHashCode.Include
    private UUID sourceAccountId; // primary key
    private String name;
    private UUID payorId; // foreign key to PayorInfo.payorId
    private String fundingRef;
    private Long lastUpdatedDateTime;
    private String currency;
}
