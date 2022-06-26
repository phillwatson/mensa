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

        // use $set to update given columns
        // encoding will ignore null values due to @JsonInclude(JsonInclude.Include.NON_NULL)
        BsonDocument updateDoc = new BsonDocument("$set", getDelta(payout));

        // if event carries a status
        if (payout.getStatus() != null) {
            // append (push) to the status history - as well as store in main body
            updateDoc.put("$push",
                new BsonDocument("statusHistory",
                    encode(new Payout.StatusHistory(payout.getStatus(), Instant.now()))));
        }

        // use upsert to create new or update existing record
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
