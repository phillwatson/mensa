package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.auditor.domain.Payment;
import com.hillayes.mensa.auditor.domain.PaymentStatus;
import com.hillayes.mensa.auditor.utils.EnumUtils;
import com.hillayes.mensa.events.events.payment.PaymentEvent;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
@Slf4j
public class PaymentMongoRepository extends BaseMongoRepository<Payment> {
    public void updateDelta(PaymentEvent paymentEvent) {
        log.debug("Updating Payment delta [paymentId: {}]", paymentEvent.getPaymentId());

        Payment payment = Payment.builder()
            .id(paymentEvent.getPaymentId())
            .payoutId(paymentEvent.getPayoutId())
            .batchId(paymentEvent.getBatchId())
            .payeeId(paymentEvent.getPayeeId())
            .memo(paymentEvent.getMemo())
            .sourceCurrency(paymentEvent.getSourceCurrency())
            .sourceAmount(paymentEvent.getSourceAmount())
            .status(EnumUtils.safeValueOf(PaymentStatus.values(), paymentEvent.getStatus()))
            .build();

        BsonDocument updateDoc = new BsonDocument("$set", getDelta(payment));
        if (payment.getStatus() != null) {
            updateDoc.put("$push",
                new BsonDocument("statusHistory",
                    encode(new Payment.StatusHistory(payment.getStatus(), Instant.now()))));
        }

        UpdateResult updateResult = mongoCollection()
            .updateOne(eq("_id", payment.getId()),
                updateDoc, new UpdateOptions().upsert(true)
            );

        log.debug("Updated Payment delta [paymentId: {}, modifiedCount: {}]",
            paymentEvent.getPaymentId(), updateResult.getModifiedCount());
    }

    private BsonDocument getDelta(Payment payment) {
        BsonDocument result = encode(payment);
        return result;
    }
}
