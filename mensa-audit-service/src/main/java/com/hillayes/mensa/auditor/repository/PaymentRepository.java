package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.events.events.payment.PaymentEvent;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
@Slf4j
public class PaymentRepository extends BaseRepository<PaymentEvent> {
    public void updateDelta(PaymentEvent payment) {
        log.debug("Updating PaymentEvent delta [paymentId: {}]", payment.getPaymentId());
        BsonDocument doc = getDelta(payment);
        UpdateResult updateResult = mongoCollection()
            .updateOne(eq("_id", payment.getPaymentId()),
                new Document("$set", doc),
                new UpdateOptions().upsert(true)
            );
        log.debug("Updated PaymentEvent delta [paymentId: {}, modifiedCount: {}]",
            payment.getPaymentId(), updateResult.getModifiedCount());
    }

    private BsonDocument getDelta(PaymentEvent payment) {
        BsonDocument result = encode(payment);
        result.put("_id", result.remove("paymentId"));
        return result;
    }
}
