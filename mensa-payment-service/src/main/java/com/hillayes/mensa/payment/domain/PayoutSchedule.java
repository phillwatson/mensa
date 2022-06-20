package com.hillayes.mensa.payment.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayoutSchedule {
    private ScheduleStatus scheduleStatus;
    private UUID scheduledByPrincipalId;
    private Instant scheduledAt;
    private Instant scheduledFor;
    private Boolean notificationsEnabled;
}
