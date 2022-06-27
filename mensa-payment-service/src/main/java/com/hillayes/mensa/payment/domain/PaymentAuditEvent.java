package com.hillayes.mensa.payment.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="payment_audit_event")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAuditEvent {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "correlation_id")
    private UUID correlationId;
    private String action;
    private String service;
    private String payload;
    @Column(name = "payload_class")
    private String payloadClass;
    private long timestamp;
    private String version;
    @Column(name = "audit_timestamp")
    private long auditTimestamp;
    @Column(name = "event_source")
    private String eventSource;
    private String principal;
}
