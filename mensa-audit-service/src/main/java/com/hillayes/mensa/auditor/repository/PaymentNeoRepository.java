package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.events.events.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PaymentNeoRepository {
    private final Driver driver;

    public void updateDelta(PaymentEvent paymentEvent) {
        Query createPayoutNode = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (payee:Payee {id:$payeeId}) " +
                "MERGE (payee)<-[r:RECEIVES]-(payout) " +
                " ON CREATE SET r.amount = $amount, r.currency = $currency " +
                " ON MATCH  SET r.amount = coalesce(r.amount, $amount), r.currency = coalesce(r.currency, $currency) " +
                "RETURN payee.id, type(r), payout.id"
        ).withParameters(Values.parameters(
                "payoutId", paymentEvent.getPayoutId().toString(),
                "payeeId", paymentEvent.getPayeeId().toString(),
                "amount", paymentEvent.getSourceAmount(),
                "currency", paymentEvent.getSourceCurrency()
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
