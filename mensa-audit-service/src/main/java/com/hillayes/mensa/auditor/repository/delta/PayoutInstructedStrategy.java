package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutInstructed;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PayoutInstructedStrategy extends DeltaStrategy {
    public PayoutInstructedStrategy() {
        super(PayoutInstructed.class);
    }

    @Override
    public UUID getEntityId(EventPacket event) {
        PayoutInstructed payload = event.getPayloadContent();
        return payload.getPayoutId();
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutInstructed payoutInstructed = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)<-[FROM]-(payout) " +
                "MERGE (payoutToPayor)-[TO]->(payout)"
        ).withParameters(Values.parameters(
                "payoutId", payoutInstructed.getPayoutId().toString(),
                "submittingPayorId", payoutInstructed.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", payoutInstructed.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", payoutInstructed.getPayoutPayorIds().getPayoutToPayorId().toString()
            )
        );

        return Optional.of(result);
    }

    @Override
    public Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutInstructed payoutInstructed = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutInstructed.getPayoutId())
            .submittingPayorId(payoutInstructed.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutInstructed.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutInstructed.getPayoutPayorIds().getPayoutToPayorId())
            .instructedDateTime(event.getTimestamp())
            .status(PayoutStatus.INSTRUCTED)
            .build();

        BsonDocument result = new BsonDocument("$set", encode(codecRegistry, payout));
        result.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.INSTRUCTED, event.getTimestamp()))));

        return Optional.of(result);
    }
}
