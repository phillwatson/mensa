package com.hillayes.mensa.auditor.service;

import com.hillayes.mensa.auditor.repository.PaymentMongoRepository;
import com.hillayes.mensa.auditor.repository.PaymentNeoRepository;
import com.hillayes.mensa.auditor.repository.delta.DeltaStrategy;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final PaymentMongoRepository paymentMongoRepository;
    private final PaymentNeoRepository paymentNeoRepository;

    private final Instance<DeltaStrategy> deltaStrategies;

    public void auditEvent(Topic topic, EventPacket event) {
        log.info("Auditing PayoutEvent [topic: {}]", topic);

        deltaStrategies.stream()
            .filter(s -> s.isApplicable(event.getPayloadClass()))
            .findAny()
            .ifPresentOrElse(
                strategy -> {
                    paymentMongoRepository.updateDelta(strategy, event);
                    paymentNeoRepository.updateDelta(strategy, event);
                    log.debug("Audited PayoutEvent [topic: {}]", topic);
                },
                () ->
                    log.info("No strategy for PayoutEvent [payloadClass: {}]", event.getPayloadClass())
            );
    }
}
