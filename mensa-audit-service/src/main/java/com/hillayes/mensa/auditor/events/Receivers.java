package com.hillayes.mensa.auditor.events;

import com.hillayes.mensa.auditor.service.AuditService;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.receiver.ConsumerFactory;
import com.hillayes.mensa.events.receiver.TopicListener;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Receivers {
    private final ConsumerFactory consumerFactory;
    private final AuditService auditService;

    void onStart(@Observes StartupEvent ev) {
        log.info("Adding event listeners");

        consumerFactory.addTopicListener(
            new TopicListener(Topic.PAYMENT_AUDIT, (EventPacket event) -> {
                log.info("Received Audit Event [topic: {}, correlationId: {}, timestamp: {}, payloadClass: {}]",
                    Topic.PAYMENT_AUDIT, event.getCorrelationId(), event.getTimestamp(), event.getPayloadClass());

                auditService.auditEvent(Topic.PAYMENT_AUDIT, event);
            })
        );
    }
}
