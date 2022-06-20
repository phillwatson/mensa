package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.events.events.payment.PayoutEvent;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
@Slf4j
public class PayoutRepository extends BaseRepository<PayoutEvent> {
    public void updateDelta(PayoutEvent payout) {
        log.debug("Updating PayoutEvent delta [payoutId: {}]", payout.getPayoutId());
        BsonDocument doc = getDelta(payout);
        UpdateResult updateResult = mongoCollection()
            .updateOne(eq("_id", payout.getPayoutId()),
                new Document("$set", doc),
                new UpdateOptions().upsert(true)
            );
        log.debug("Updated PayoutEvent delta [payoutId: {}, modifiedCount: {}]",
            payout.getPayoutId(), updateResult.getModifiedCount());
    }

    private BsonDocument getDelta(PayoutEvent payout) {
        BsonDocument result = encode(payout);
        result.put("_id", result.remove("payoutId"));
        return result;
    }
}
