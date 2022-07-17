package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payment;
import com.hillayes.mensa.auditor.domain.PaymentStatus;
import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PaymentSubmittedAccepted;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class PaymentAcceptedStrategy extends AbstractDeltaStrategy {
    public PaymentAcceptedStrategy() {
        super(PaymentSubmittedAccepted.class);
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PaymentSubmittedAccepted paymentAccepted = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[:SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)-[:FROM]->(payout) " +
                "MERGE (payoutToPayor)<-[:TO]-(payout) " +
                "MERGE (payee:Payee {id:$payeeId}) " +
                "MERGE (payout)-[r:PAID {paymentId:$paymentId}]->(payee) " +
                "  ON CREATE SET r.railsPaymentId=$railsPaymentId" +
                "  ON MATCH SET r.railsPaymentId=$railsPaymentId "
        ).withParameters(Values.parameters(
                "payoutId", paymentAccepted.getPayoutId().toString(),
                "submittingPayorId", paymentAccepted.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", paymentAccepted.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", paymentAccepted.getPayoutPayorIds().getPayoutToPayorId().toString(),
                "payeeId", paymentAccepted.getPayeeId().toString(),
                "paymentId", paymentAccepted.getPaymentId().toString(),
                "railsPaymentId", paymentAccepted.getRailsPaymentId()
            )
        );

        return Optional.of(result);
    }

    @Override
    public <T> List<WriteModel<? extends T>> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PaymentSubmittedAccepted paymentAccepted = event.getPayloadContent();

        Payment payment = Payment.builder()
            .id(paymentAccepted.getPaymentId())
            .payoutId(paymentAccepted.getPayoutId())
            .payeeId(paymentAccepted.getPayeeId())
            .railsPaymentId(paymentAccepted.getRailsPaymentId())
            .status(PaymentStatus.ACCEPTED)
            .build();

        BsonDocument update = new BsonDocument("$set", encode(codecRegistry, payment));
        update.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.ACCEPTED, event.getTimestamp()))));

        return List.of(
            new UpdateOneModel<>(eq(paymentAccepted.getPaymentId()), update, UPSERT_OPTION)
        );
    }
}
