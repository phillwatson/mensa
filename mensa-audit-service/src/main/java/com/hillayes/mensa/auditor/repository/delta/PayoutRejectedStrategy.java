package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutRejected;
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
public class PayoutRejectedStrategy extends AbstractDeltaStrategy {
    public PayoutRejectedStrategy() {
        super(PayoutRejected.class);
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutRejected payoutRejected = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[:SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)-[:FROM]->(payout) " +
                "MERGE (payoutToPayor)<-[:TO]-(payout)"
        ).withParameters(Values.parameters(
                "payoutId", payoutRejected.getPayoutId().toString(),
                "submittingPayorId", payoutRejected.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", payoutRejected.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", payoutRejected.getPayoutPayorIds().getPayoutToPayorId().toString()
            )
        );

        return Optional.of(result);
    }

    @Override
    public <T> List<WriteModel<? extends T>> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutRejected payoutRejected = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutRejected.getPayoutId())
            .submittingPayorId(payoutRejected.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutRejected.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutRejected.getPayoutPayorIds().getPayoutToPayorId())
            .status(PayoutStatus.REJECTED)
            .build();

        BsonDocument update = new BsonDocument("$set", encode(codecRegistry, payout));
        update.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.REJECTED, event.getTimestamp()))));

        return List.of(new UpdateOneModel<>(
            eq(payoutRejected.getPayoutId()), update, UPSERT_OPTION
        ));
    }
}
