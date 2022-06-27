package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Batch;
import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PayoutAccepted;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;
import org.neo4j.driver.Values;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class PayoutAcceptedStrategy extends DeltaStrategy {
    public PayoutAcceptedStrategy() {
        super(PayoutAccepted.class);
    }

    @Override
    public UUID getEntityId(EventPacket event) {
        PayoutAccepted payload = event.getPayloadContent();
        return payload.getPayoutId();
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PayoutAccepted payoutAccepted = event.getPayloadContent();

        Query result = new Query(
            "MERGE (payout:Payout {id:$payoutId}) " +
                "MERGE (submittingPayor:Payor {id:$submittingPayorId}) " +
                "MERGE (payoutFromPayor:Payor {id:$payoutFromPayorId}) " +
                "MERGE (payoutToPayor:Payor {id:$payoutToPayorId}) " +
                "MERGE (submittingPayor)-[SUBMITTED]->(payout) " +
                "MERGE (payoutFromPayor)<-[FROM]-(payout) " +
                "MERGE (payoutToPayor)-[TO]->(payout)"
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
    public Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PayoutAccepted payoutAccepted = event.getPayloadContent();

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

        BsonDocument result = new BsonDocument("$set", encode(codecRegistry, payout));
        result.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.ACCEPTED, event.getTimestamp()))));

        return Optional.of(result);
    }
}
