package com.hillayes.mensa.payment.service;

import com.hillayes.mensa.payment.domain.PaymentAuditEvent;
import com.hillayes.mensa.payment.events.PaymentEventSender;
import com.hillayes.mensa.payment.repository.PaymentAuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentEventSender paymentEventSender;
    private final PaymentAuditEventRepository paymentAuditEventRepository;

    public List<PaymentAuditEvent> uploadPaymentEvents() {
        log.info("Uploading payment-audit-events");

        return paymentAuditEventRepository.findAll().stream()
            .peek(paymentEventSender::sendPayment)
            .collect(Collectors.toList());
    }

    public Optional<PaymentAuditEvent> uploadPaymentEvent(UUID id) {
        log.info("Uploading payment-audit-event [id: {}]", id);
        Optional<PaymentAuditEvent> result = paymentAuditEventRepository.findById(id);
        result.ifPresent(paymentEventSender::sendPayment);
        return result;
    }
}
