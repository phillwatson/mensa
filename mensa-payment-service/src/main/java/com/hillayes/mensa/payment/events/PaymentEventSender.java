package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.outbox.sender.EventSender;
import com.hillayes.mensa.payment.domain.PaymentAuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Future;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PaymentEventSender {
    private final EventSender eventSender;

    public void sendPayment(PaymentAuditEvent payment) {
        log.debug("Sending payment-audit event [id: {}, action: {}, payload: {}]",
            payment.getId(), payment.getAction(), payment.getPayloadClass());
    }

    private String mutatePayloadClass(String originalClassname) {
        int p = originalClassname.lastIndexOf('.') + 1;
        String name = originalClassname.substring(p);
        return "com.hillayes.mensa.events.events.payout." + name;
    }
}
