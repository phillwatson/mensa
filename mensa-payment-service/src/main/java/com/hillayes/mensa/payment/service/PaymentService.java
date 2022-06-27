package com.hillayes.mensa.payment.service;

import com.hillayes.mensa.payment.domain.PaymentAuditEvent;
import com.hillayes.mensa.payment.events.PaymentEventSender;
import com.hillayes.mensa.payment.repository.PaymentAuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentEventSender paymentEventSender;
    private final PaymentAuditEventRepository paymentAuditEventRepository;

    public Optional<PaymentAuditEvent> uploadPaymentEvent(UUID id) {
        log.info("Uploading payment-audit-event [id: {}]", id);
        return paymentAuditEventRepository.findById(id);
    }
}
