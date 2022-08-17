package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.outbox.sender.EventSender;
import com.hillayes.mensa.payment.domain.PaymentAuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PaymentEventSender {
    private final EventSender eventSender;

    public void sendPayment(PaymentAuditEvent payment) {
        log.debug("Sending payment-audit event [id: {}, action: {}, payload: {}]",
            payment.getId(), payment.getAction(), payment.getPayloadClass());

        String payloadClass = mutatePayloadClass(payment.getPayloadClass());

        EventPacket eventPacket = new EventPacket(payment.getId(), Topic.PAYMENT_AUDIT,
                payment.getCorrelationId().toString(),
                0, Instant.ofEpochMilli(payment.getTimestamp()),
                payloadClass, payment.getPayload());

        eventSender.send(Topic.PAYMENT_AUDIT, eventPacket);
    }

    private String mutatePayloadClass(String originalClassname) {
        int p = originalClassname.lastIndexOf('.') + 1;
        String name = originalClassname.substring(p);
        return "com.hillayes.mensa.events.events.payout." + name;
    }
}
