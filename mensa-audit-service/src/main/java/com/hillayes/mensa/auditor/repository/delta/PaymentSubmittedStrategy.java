package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.auditor.domain.Payment;
import com.hillayes.mensa.auditor.domain.PaymentStatus;
import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.events.payout.PaymentSubmitted;
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
public class PaymentSubmittedStrategy extends AbstractDeltaStrategy {
    public PaymentSubmittedStrategy() {
        super(PaymentSubmitted.class);
    }

    @Override
    public Optional<Query> neoDelta(EventPacket event) {
        PaymentSubmitted paymentSubmitted = event.getPayloadContent();

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
                "  ON CREATE SET r.railsId=$railsId, r.currency=$currency, r.amount=$amount" +
                "  ON MATCH SET r.railsId=$railsId, r.currency=$currency, r.amount=$amount "
        ).withParameters(Values.parameters(
                "payoutId", paymentSubmitted.getPayoutId().toString(),
                "submittingPayorId", paymentSubmitted.getPayoutPayorIds().getSubmittingPayorId().toString(),
                "payoutFromPayorId", paymentSubmitted.getPayoutPayorIds().getPayoutFromPayorId().toString(),
                "payoutToPayorId", paymentSubmitted.getPayoutPayorIds().getPayoutToPayorId().toString(),
                "payeeId", paymentSubmitted.getPayeeId().toString(),
                "paymentId", paymentSubmitted.getPaymentId().toString(),
                "railsId", paymentSubmitted.getRailsId(),
                "currency", currencyCode(paymentSubmitted.getCurrency()),
                "amount", paymentSubmitted.getAmount())
        );

        return Optional.of(result);
    }

    @Override
    public <T> List<WriteModel<? extends T>> mongoDelta(CodecRegistry codecRegistry, EventPacket event) {
        PaymentSubmitted paymentSubmitted = event.getPayloadContent();

        Payment payment = Payment.builder()
            .id(paymentSubmitted.getPaymentId())
            .payoutId(paymentSubmitted.getPayoutId())
            .payeeId(paymentSubmitted.getPayeeId())
            .accountName(paymentSubmitted.getAccountName())
            .accountNumber(paymentSubmitted.getAccountNumber())
            .payeeAddressCountryCode(paymentSubmitted.getPayeeAddressCountryCode())
            .paymentChannelId(paymentSubmitted.getPaymentChannelId())
            .paymentChannelName(paymentSubmitted.getPaymentChannelName())
            .sourceAccountId(paymentSubmitted.getSourceAccountId())
            .railsId(paymentSubmitted.getRailsId())
            .sourceCurrency(currencyCode(paymentSubmitted.getCurrency()))
            .sourceAmount(paymentSubmitted.getAmount())
            .paymentCurrency(currencyCode(paymentSubmitted.getSettlementCurrency()))
            .status(PaymentStatus.SUBMITTED)
            .build();

        BsonDocument update = new BsonDocument("$set", encode(codecRegistry, payment));
        update.put("$push",
            new BsonDocument("statusHistory",
                encode(codecRegistry, new Payout.StatusHistory(PayoutStatus.SUBMITTED, event.getTimestamp()))));

        return List.of(
            new UpdateOneModel<>(eq(paymentSubmitted.getPaymentId()), update, UPSERT_OPTION)
        );
    }
}
