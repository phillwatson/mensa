package com.hillayes.mensa.auditor.service;

import com.hillayes.mensa.auditor.repository.PaymentMongoRepository;
import com.hillayes.mensa.auditor.repository.PaymentNeoRepository;
import com.hillayes.mensa.auditor.repository.PayoutMongoRepository;
import com.hillayes.mensa.auditor.repository.PayoutNeoRepository;
import com.hillayes.mensa.auditor.repository.delta.DeltaStrategy;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final PayoutMongoRepository payoutMongoRepository;
    private final PayoutNeoRepository payoutNeoRepository;

    private final List<DeltaStrategy> deltaStrategies;

    public void auditEvent(Topic topic, EventPacket event) {
        log.info("Auditing PayoutEvent [topic: {}]", topic);

        deltaStrategies.stream()
            .filter(s -> s.isApplicable(event.getPayloadClass()))
            .findAny()
            .ifPresentOrElse(
                strategy -> {
                    payoutMongoRepository.updateDelta(strategy, event);
                    payoutNeoRepository.updateDelta(strategy, event);
                    log.debug("Audited PayoutEvent [topic: {}]", topic);
                },
                () ->
                    log.info("No strategy for PayoutEvent [payloadClass: {}]", event.getPayloadClass())
            );
    }
}
