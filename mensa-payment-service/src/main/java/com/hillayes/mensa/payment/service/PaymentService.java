package com.hillayes.mensa.payment.service;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.payment.domain.Payment;
import com.hillayes.mensa.payment.events.PaymentEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentEventSender paymentEventSender;

    public Payment createPayment(Payment payment) {
        log.info("Creating payment [payeeId: {}, memo: {}]", payment.getPayeeId(), payment.getMemo());

        payment.setPaymentId(randomUUID());
        paymentEventSender.sendPayment(payment);

        log.debug("Created payment [paymentId: {}, payeeId: {}, memo: {}]",
            payment.getPaymentId(), payment.getPayeeId(), payment.getMemo());
        return payment;
    }

    public Payment updatePayment(UUID id, Payment payment) {
        log.info("Updating payment [paymentId: {}, payeeId: {}, memo: {}]",
            id, payment.getPayeeId(), payment.getMemo());

        payment.setPaymentId(id);
        paymentEventSender.sendPayment(payment);

        log.debug("Updated payment [paymentId: {}, payeeId: {}, memo: {}]",
            payment.getPaymentId(), payment.getPayeeId(), payment.getMemo());
        return payment;
    }
}
