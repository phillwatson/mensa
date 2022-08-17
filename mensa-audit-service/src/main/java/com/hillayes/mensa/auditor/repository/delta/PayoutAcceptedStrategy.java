package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Batch;
import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutAccepted;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class PayoutAcceptedStrategy extends AbstractDeltaStrategy {
    public PayoutAcceptedStrategy() {
        super(PayoutAccepted.class);
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutAccepted payoutAccepted = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[:SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)-[:FROM]->(payout) " +
                "MERGE (payoutToPayor)<-[:TO]-(payout)"
        ).withParameters(Values.parameters(
                "payoutId", payoutAccepted.getPayoutId().toString(),
                "submittingPayorId", payoutAccepted.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", payoutAccepted.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", payoutAccepted.getPayoutPayorIds().getPayoutToPayorId().toString()
            )
        );

        return Optional.of(result);
    }

    @Override
    public <T> List<WriteModel<? extends T>> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutAccepted payoutAccepted = event.getPayloadContent();

        // https://mongoplayground.net/p/7mhzctmcS6O

        List<Batch> batches = payoutAccepted.getBatches().stream()
            .map(summary -> Batch.builder()
                .id(summary.getBatchId())
                .totalAcceptedPayments(summary.getNumberOfAcceptedPayments())
                .totalRejectedPayments(summary.getNumberOfRejectedPayments())
                .sourceCurrency(summary.getSourceCurrency() == null ? null : summary.getSourceCurrency().getCurrencyCode())
                .build()
            )
            .collect(Collectors.toList());

        Payout payout = Payout.builder()
            .id(payoutAccepted.getPayoutId())
            .submittingPayorId(payoutAccepted.getPayoutPayorIds().getSubmittingPayorId())
            .payoutFromPayorId(payoutAccepted.getPayoutPayorIds().getPayoutFromPayorId())
            .payoutToPayorId(payoutAccepted.getPayoutPayorIds().getPayoutToPayorId())
            .totalAcceptedPayments(payoutAccepted.getNumberOfAcceptedPayments())
            .totalRejectedPayments(payoutAccepted.getNumberOfRejectedPayments())
            .batches(batches)
            .status(PayoutStatus.ACCEPTED)
            .build();

        BsonDocument update = new BsonDocument("$set", encode(codecRegistry, payout));
        update.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.ACCEPTED, event.getTimestamp()))));

        return List.of(
            new UpdateOneModel<>(eq(payoutAccepted.getPayoutId()), update, UPSERT_OPTION)
        );
    }
}
