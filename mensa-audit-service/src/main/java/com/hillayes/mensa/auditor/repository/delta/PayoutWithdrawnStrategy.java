package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutWithdrawn;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PayoutWithdrawnStrategy extends DeltaStrategy {
    public PayoutWithdrawnStrategy() {
        super(PayoutWithdrawn.class);
    }

    @Override
    public UUID getEntityId(EventPacket event) {
        PayoutWithdrawn payload = event.getPayloadContent();
        return payload.getPayoutId();
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutWithdrawn payoutWithdrawn = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)<-[FROM]-(payout) " +
                "MERGE (payoutToPayor)-[TO]->(payout)"
        ).withParameters(Values.parameters(
                "payoutId", payoutWithdrawn.getPayoutId().toString(),
                "submittingPayorId", payoutWithdrawn.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", payoutWithdrawn.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", payoutWithdrawn.getPayoutPayorIds().getPayoutToPayorId().toString()
            )
        );

        return Optional.of(result);
    }

    @Override
    public Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutWithdrawn payoutWithdrawn = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutWithdrawn.getPayoutId())
            .submittingPayorId(payoutWithdrawn.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutWithdrawn.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutWithdrawn.getPayoutPayorIds().getPayoutToPayorId())
            .withdrawalReason(payoutWithdrawn.getReason())
            .withdrawnDateTime(event.getTimestamp())
            .status(PayoutStatus.WITHDRAWN)
            .build();

        BsonDocument result = new BsonDocument("$set", encode(codecRegistry, payout));
        result.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.WITHDRAWN, event.getTimestamp()))));
        result.put("$inc",
            new BsonDocument("totalWithdrawnPayments", new BsonInt32(1)));

        return Optional.of(result);
    }
}
