package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.domain.PayoutStatus;
import com.hillayes.mensa.auditor.utils.EnumUtils;
import com.hillayes.mensa.events.events.payment.PayoutEvent;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;

import javax.enterprise.context.ApplicationScoped;

import java.time.Instant;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
@Slf4j
public class PayoutMongoRepository extends BaseMongoRepository<Payout> {
    public void updateDelta(PayoutEvent payoutEvent) {
        log.debug("Updating Payout delta [payoutId: {}]", payoutEvent.getPayoutId());

        Payout payout = Payout.builder()
            .id(payoutEvent.getPayoutId())
            .submittingPayorId(payoutEvent.getSubmittingPayorId())
            .payoutFromPayorId(payoutEvent.getPayoutFromPayorId())
            .payoutToPayorId(payoutEvent.getPayoutToPayorId())
            .payoutMemo(payoutEvent.getPayoutMemo())
            .status(EnumUtils.safeValueOf(PayoutStatus.values(), payoutEvent.getStatus()))
            .submittedDateTime(payoutEvent.getSubmittedDateTime())
            .quotedDateTime(payoutEvent.getQuotedDateTime())
            .instructedDateTime(payoutEvent.getInstructedDateTime())
            .withdrawnDateTime(payoutEvent.getWithdrawnDateTime())
            .build();

        BsonDocument updateDoc = new BsonDocument("$set", getDelta(payout));
        if (payout.getStatus() != null) {
            updateDoc.put("$push",
                new BsonDocument("statusHistory",
                    encode(new Payout.StatusHistory(payout.getStatus(), Instant.now()))));
        }

        UpdateResult updateResult = mongoCollection()
            .updateOne(eq("_id", payout.getId()),
                updateDoc, new UpdateOptions().upsert(true)
            );

        log.debug("Updated Payout delta [payoutId: {}, modifiedCount: {}]",
            payoutEvent.getPayoutId(), updateResult.getModifiedCount());
    }

    private BsonDocument getDelta(Payout payout) {
        BsonDocument result = encode(payout);
        return result;
    }
}
