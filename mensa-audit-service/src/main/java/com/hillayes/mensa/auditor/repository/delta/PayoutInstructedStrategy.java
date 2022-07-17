package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutInstructed;
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
public class PayoutInstructedStrategy extends AbstractDeltaStrategy {
    public PayoutInstructedStrategy() {
        super(PayoutInstructed.class);
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutInstructed payoutInstructed = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[:SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)-[:FROM]->(payout) " +
                "MERGE (payoutToPayor)<-[:TO]-(payout)"
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
    public <T> List<WriteModel<? extends T>> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutInstructed payoutInstructed = event.getPayloadContent();

        Payout payout = Payout.builder()
            .id(payoutInstructed.getPayoutId())
            .submittingPayorId(payoutInstructed.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutInstructed.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutInstructed.getPayoutPayorIds().getPayoutToPayorId())
            .instructedDateTime(event.getTimestamp())
            .status(PayoutStatus.INSTRUCTED)
            .build();

        BsonDocument update = new BsonDocument("$set", encode(codecRegistry, payout));
        update.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.INSTRUCTED, event.getTimestamp()))));

        return List.of(new UpdateOneModel<>(
            eq(payoutInstructed.getPayoutId()), update, UPSERT_OPTION
        ));
    }
}
