package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.events.events.payment.PayoutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.enterprise.context.ApplicationScoped;

import static com.hillayes.mensa.auditor.utils.DateUtils.toLocalDateTime;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PayoutNeoRepository {
    private final Driver driver;

    public void updateDelta(PayoutEvent payoutEvent) {
        Query createPayoutNode = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                " ON CREATE SET" +
                "  payout.submittedDateTime = $submittedDateTime," +
                "  payout.quotedDateTime = $quotedDateTime," +
                "  payout.instructedDateTime = $instructedDateTime," +
                "  payout.withdrawnDateTime = $withdrawnDateTime " +
                " ON MATCH SET" +
                "  payout.submittedDateTime = coalesce(payout.submittedDateTime, $submittedDateTime)," +
                "  payout.quotedDateTime = coalesce(payout.quotedDateTime, $quotedDateTime)," +
                "  payout.instructedDateTime = coalesce(payout.instructedDateTime, $instructedDateTime)," +
                "  payout.withdrawnDateTime = coalesce(payout.withdrawnDateTime, $withdrawnDateTime) " +
                "MERGE (payor:Payor {id:$payorId}) " +
                "MERGE (payor)-[r:MAKES]->(payout) " +
                "RETURN payor.id, type(r), payout.id"
        ).withParameters(Values.parameters(
                "payoutId", payoutEvent.getPayoutId().toString(),
                "payorId", payoutEvent.getPayoutFromPayorId().toString(),
                "submittedDateTime", toLocalDateTime(payoutEvent.getSubmittedDateTime()).orElse(null),
                "quotedDateTime", toLocalDateTime(payoutEvent.getQuotedDateTime()).orElse(null),
                "instructedDateTime", toLocalDateTime(payoutEvent.getInstructedDateTime()).orElse(null),
                "withdrawnDateTime", toLocalDateTime(payoutEvent.getWithdrawnDateTime()).orElse(null)
            )
        );

        driver.session().writeTransaction(tx -> {
            tx.run(createPayoutNode).forEachRemaining(r ->
                log.debug("Cypher result: {}", r.asMap())
            );
            return null;
        });
    }
}
