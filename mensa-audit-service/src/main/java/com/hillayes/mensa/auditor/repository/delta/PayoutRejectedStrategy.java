package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutRejected;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PayoutRejectedStrategy extends DeltaStrategy {
    public PayoutRejectedStrategy() {
        super(PayoutRejected.class);
    }

    @Override
    public UUID getEntityId(EventPacket event) {
        PayoutRejected payload = event.getPayloadContent();
        return payload.getPayoutId();
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutRejected payoutRejected = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)<-[FROM]-(payout) " +
                "MERGE (payoutToPayor)-[TO]->(payout)"
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
    public Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutRejected payoutRejected = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutRejected.getPayoutId())
            .submittingPayorId(payoutRejected.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutRejected.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutRejected.getPayoutPayorIds().getPayoutToPayorId())
            .status(PayoutStatus.REJECTED)
            .build();

        BsonDocument result = new BsonDocument("$set", encode(codecRegistry, payout));
        result.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.REJECTED, event.getTimestamp()))));

        return Optional.of(result);
    }
}
