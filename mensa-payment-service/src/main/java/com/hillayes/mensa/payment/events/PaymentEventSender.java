package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.payment.PaymentEvent;
import com.hillayes.mensa.events.sender.EventSender;
import com.hillayes.mensa.payment.domain.Payment;
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

    public Future<RecordMetadata> sendPaymentCreated(Payment payment) {
        log.debug("Sending PaymentCreated event [paymentId: {}, payeeId: {}, memo: {}]",
            payment.getPaymentId(), payment.getPayeeId(), payment.getMemo());
        return eventSender.send(Topic.PAYMENT_CREATED, PaymentEvent.builder()
            .paymentId(payment.getPaymentId())
            .batchId(payment.getBatchId())
            .payeeId(payment.getPayeeId())
            .payoutId(payment.getPayoutId())
            .memo(payment.getMemo())
            .sourceCurrency(payment.getSourceCurrency())
            .sourceAmount(payment.getSourceAmount())
            .build());
    }

    public Future<RecordMetadata> sendPaymentAccepted(Payment payment) {
        log.debug("Sending PaymentAccepted event [paymentId: {}, payeeId: {}, memo: {}]",
            payment.getPaymentId(), payment.getPayeeId(), payment.getMemo());
        return eventSender.send(Topic.PAYMENT_ACCEPTED, PaymentEvent.builder()
            .paymentId(payment.getPaymentId())
            .batchId(payment.getBatchId())
            .payeeId(payment.getPayeeId())
            .payoutId(payment.getPayoutId())
            .memo(payment.getMemo())
            .sourceCurrency(payment.getSourceCurrency())
            .sourceAmount(payment.getSourceAmount())
            .build());
    }
}
