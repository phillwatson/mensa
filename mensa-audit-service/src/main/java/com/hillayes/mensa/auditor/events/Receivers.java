package com.hillayes.mensa.auditor.events;

import com.hillayes.mensa.auditor.service.AuditService;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.payment.PaymentEvent;
import com.hillayes.mensa.events.events.payment.PayoutEvent;
import com.hillayes.mensa.events.receiver.ConsumerFactory;
import com.hillayes.mensa.events.receiver.TopicListener;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Receivers {
    private final ConsumerFactory consumerFactory;
    private final AuditService auditService;

    void onStart(@Observes StartupEvent ev) {
        log.info("Adding event listeners");

        List.of(
            // send the following events to the same service method
            Topic.PAYOUT_INSTRUCTED,
            Topic.PAYOUT_CONFIRMED,
            Topic.PAYOUT_INCOMPLETE,
            Topic.PAYOUT_COMPLETED,
            Topic.PAYOUT_SUBMITTED,
            Topic.PAYOUT_ACCEPTED,
            Topic.PAYOUT_REJECTED,
            Topic.PAYOUT_QUOTED,
            Topic.PAYOUT_WITHDRAWN
        ).forEach(topic ->
            consumerFactory.addTopicListener(
                new TopicListener(topic, (EventPacket event) -> {
                    PayoutEvent payload = event.getPayloadContent();
                    log.info("Received PayoutEvent [topic: {}, correlationId: {}, timestamp: {}, payoutId: {}]",
                        topic, event.getCorrelationId(), event.getTimestamp(), payload.getPayoutId());

                    auditService.auditEvent(topic, payload);
                })
            )
        );

        List.of(
            // send the following events to the same service method
            Topic.PAYMENT_BANK_PAYMENT_REQUESTED,
            Topic.PAYMENT_ACCEPTED_BY_RAILS,
            Topic.PAYMENT_CONFIRMED,
            Topic.PAYMENT_RETURNED,
            Topic.PAYMENT_SUBMITTED,
            Topic.PAYMENT_CANCELLED,
            Topic.PAYMENT_ACCEPTED,
            Topic.PAYMENT_REJECTED,
            Topic.PAYMENT_AWAITING_FUNDS,
            Topic.PAYMENT_FUNDED,
            Topic.PAYMENT_UNFUNDED,
            Topic.PAYMENT_WITHDRAWN,
            Topic.PAYMENT_FAILED
        ).forEach(topic ->
            consumerFactory.addTopicListener(
                new TopicListener(topic, (EventPacket event) -> {
                    PaymentEvent payload = event.getPayloadContent();
                    log.info("Received PaymentEvent [topic: {}, correlationId: {}, timestamp: {}, paymentId: {}]",
                        topic, event.getCorrelationId(), event.getTimestamp(), payload.getPaymentId());

                    auditService.auditEvent(topic, payload);
                })
            )
        );
    }
}
