package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.payment.PaymentEvent;
import com.hillayes.mensa.events.sender.EventSender;
import com.hillayes.mensa.payment.domain.Payment;
import com.hillayes.mensa.payment.domain.PaymentStatus;
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

    public Future<RecordMetadata> sendPayment(Payment payment) {
        Topic topic = getTopic(payment.getStatus());

        log.debug("Sending {} event [paymentId: {}, payeeId: {}, memo: {}]",
            topic, payment.getPaymentId(), payment.getPayeeId(), payment.getMemo());
        return eventSender.send(topic, marshal(payment));
    }

    private PaymentEvent marshal(Payment payment) {
        return PaymentEvent.builder()
            .paymentId(payment.getPaymentId())
            .batchId(payment.getBatchId())
            .payeeId(payment.getPayeeId())
            .payoutId(payment.getPayoutId())
            .memo(payment.getMemo())
            .sourceCurrency(payment.getSourceCurrency())
            .sourceAmount(payment.getSourceAmount())
            .status((payment.getStatus() == null) ? null : payment.getStatus().name())
            .build();
    }
    
    private Topic getTopic(PaymentStatus status) {
        if (status != null) {
            switch (status) {
                case BANK_PAYMENT_REQUESTED:
                    return Topic.PAYMENT_BANK_PAYMENT_REQUESTED;
                case ACCEPTED_BY_RAILS:
                    return Topic.PAYMENT_ACCEPTED_BY_RAILS;
                case CONFIRMED:
                    return Topic.PAYMENT_CONFIRMED;
                case RETURNED:
                    return Topic.PAYMENT_RETURNED;
                case SUBMITTED:
                    return Topic.PAYMENT_SUBMITTED;
                case CANCELLED:
                    return Topic.PAYMENT_CANCELLED;
                case ACCEPTED:
                    return Topic.PAYMENT_ACCEPTED;
                case REJECTED:
                    return Topic.PAYMENT_REJECTED;
                case AWAITING_FUNDS:
                    return Topic.PAYMENT_AWAITING_FUNDS;
                case FUNDED:
                    return Topic.PAYMENT_FUNDED;
                case UNFUNDED:
                    return Topic.PAYMENT_UNFUNDED;
                case WITHDRAWN:
                    return Topic.PAYMENT_WITHDRAWN;
                case FAILED:
                    return Topic.PAYMENT_FAILED;
            }
        }
        return Topic.PAYMENT_BANK_PAYMENT_REQUESTED;
    }
}
