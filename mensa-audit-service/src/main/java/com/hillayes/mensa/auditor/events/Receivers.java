package com.hillayes.mensa.auditor.events;

import com.hillayes.mensa.auditor.service.AuditService;
import com.hillayes.mensa.events.domain.EventPacket;
import io.smallrye.reactive.messaging.annotations.Blocking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Receivers {
    private final AuditService auditService;

    @Incoming("payment_audit")
    @Blocking
    public void paymentAuditTopicListener(EventPacket event) {
        log.info("Received Audit Event [topic: {}, correlationId: {}, timestamp: {}, payloadClass: {}]",
            event.getTopic(), event.getCorrelationId(), event.getTimestamp(), event.getPayloadClass());

        auditService.auditEvent(event.getTopic(), event);
    }
}
