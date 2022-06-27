package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutQuoted;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PayoutQuotedStrategy extends DeltaStrategy {
    public PayoutQuotedStrategy() {
        super(PayoutQuoted.class);
    }

    @Override
    public UUID getEntityId(EventPacket event) {
        PayoutQuoted payload = event.getPayloadContent();
        return payload.getPayoutId();
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutQuoted payoutQuoted = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)<-[FROM]-(payout) " +
                "MERGE (payoutToPayor)-[TO]->(payout)"
        ).withParameters(Values.parameters(
                "payoutId", payoutQuoted.getPayoutId().toString(),
                "submittingPayorId", payoutQuoted.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", payoutQuoted.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", payoutQuoted.getPayoutPayorIds().getPayoutToPayorId().toString()
            )
        );

        return Optional.of(result);
    }

    @Override
    public Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutQuoted payoutQuoted = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutQuoted.getPayoutId())
            .submittingPayorId(payoutQuoted.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutQuoted.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutQuoted.getPayoutPayorIds().getPayoutToPayorId())
            .quotedDateTime(event.getTimestamp())
            .status(PayoutStatus.QUOTED)
            .build();

        BsonDocument result = new BsonDocument("$set", encode(codecRegistry, payout));
        result.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.QUOTED, event.getTimestamp()))));

        return Optional.of(result);
    }
}
