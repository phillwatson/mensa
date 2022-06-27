package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutCompleted;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PayoutCompletedStrategy extends DeltaStrategy {
    public PayoutCompletedStrategy() {
        super(PayoutCompleted.class);
    }

    @Override
    public UUID getEntityId(EventPacket event) {
        PayoutCompleted payload = event.getPayloadContent();
        return payload.getPayoutId();
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutCompleted payoutCompleted = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)<-[FROM]-(payout) " +
                "MERGE (payoutToPayor)-[TO]->(payout)"
        ).withParameters(Values.parameters(
                "payoutId", payoutCompleted.getPayoutId().toString(),
                "submittingPayorId", payoutCompleted.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", payoutCompleted.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", payoutCompleted.getPayoutPayorIds().getPayoutToPayorId().toString()
            )
        );

        return Optional.of(result);
    }

    @Override
    public Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutCompleted payoutCompleted = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutCompleted.getPayoutId())
            .submittingPayorId(payoutCompleted.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutCompleted.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutCompleted.getPayoutPayorIds().getPayoutToPayorId())
            .status(PayoutStatus.COMPLETED)
            .build();

        BsonDocument result = new BsonDocument("$set", encode(codecRegistry, payout));
        result.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.COMPLETED, event.getTimestamp()))));

        return Optional.of(result);
    }
}
