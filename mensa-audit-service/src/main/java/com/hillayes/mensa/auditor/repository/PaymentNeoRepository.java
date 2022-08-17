package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.auditor.repository.delta.DeltaStrategy;
import com.hillayes.mensa.events.domain.EventPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PaymentNeoRepository {
    private final Driver driver;

    public boolean updateDelta(DeltaStrategy deltaStrategy, EventPacket event) {
        return deltaStrategy.neoDelta(event)
            .map(query -> {
                log.trace("Updating payment [{}]", query);

                return driver.session().writeTransaction(tx -> {
                    tx.run(query).forEachRemaining(r ->
                        log.trace("Updated payment [result: {}]", r.asMap())
                    );
                    return true;
                });
            })
            .orElseGet(() -> {
                log.trace("No delta generated");
                return false;
            });
    }
}
