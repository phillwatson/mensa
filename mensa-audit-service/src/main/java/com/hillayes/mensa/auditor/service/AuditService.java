package com.hillayes.mensa.auditor.service;

import com.hillayes.mensa.auditor.repository.PaymentMongoRepository;
import com.hillayes.mensa.auditor.repository.PaymentNeoRepository;
import com.hillayes.mensa.auditor.repository.PayoutMongoRepository;
import com.hillayes.mensa.auditor.repository.PayoutNeoRepository;
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
    private final PayoutMongoRepository payoutMongoRepository;
    private final PayoutNeoRepository payoutNeoRepository;
    private final PaymentMongoRepository paymentMongoRepository;
    private final PaymentNeoRepository paymentNeoRepository;

    public void auditEvent(Topic topic, PayoutEvent event) {
        log.info("Auditing PayoutEvent [topic: {}, payoutId: {}, memo: {}]", topic, event.getPayoutId(), event.getPayoutMemo());
        payoutMongoRepository.updateDelta(event);
        payoutNeoRepository.updateDelta(event);
        log.debug("Audited PayoutEvent [topic: {}, payoutId: {}, memo: {}]", topic, event.getPayoutId(), event.getPayoutMemo());
    }

    public void auditEvent(Topic topic, PaymentEvent event) {
        log.info("Auditing PaymentEvent [topic: {}, paymentId: {}, payoutId: {}, memo: {}]", topic, event.getPaymentId(), event.getPayoutId(), event.getMemo());
        paymentMongoRepository.updateDelta(event);
        paymentNeoRepository.updateDelta(event);
        log.debug("Auditing PaymentEvent [topic: {}, paymentId: {}, payoutId: {}, memo: {}]", topic, event.getPaymentId(), event.getPayoutId(), event.getMemo());
    }
}
