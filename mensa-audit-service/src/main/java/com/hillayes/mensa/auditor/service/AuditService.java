package com.hillayes.mensa.auditor.service;

import com.hillayes.mensa.auditor.repository.PaymentRepository;
import com.hillayes.mensa.auditor.repository.PayoutRepository;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.payment.PaymentEvent;
import com.hillayes.mensa.events.events.payment.PayoutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final PayoutRepository payoutRepository;
    private final PaymentRepository paymentRepository;

    public void auditEvent(Topic topic, PayoutEvent event) {
        log.info("Auditing PayoutEvent [topic: {}, payoutId: {}, memo: {}]", topic, event.getPayoutId(), event.getPayoutMemo());
        payoutRepository.updateDelta(event);
        log.debug("Audited PayoutEvent [topic: {}, payoutId: {}, memo: {}]", topic, event.getPayoutId(), event.getPayoutMemo());
    }

    public void auditEvent(Topic topic, PaymentEvent event) {
        log.info("Auditing PaymentEvent [topic: {}, paymentId: {}, payoutId: {}, memo: {}]", topic, event.getPaymentId(), event.getPayoutId(), event.getMemo());
        paymentRepository.updateDelta(event);
        log.debug("Auditing PaymentEvent [topic: {}, paymentId: {}, payoutId: {}, memo: {}]", topic, event.getPaymentId(), event.getPayoutId(), event.getMemo());
    }
}
