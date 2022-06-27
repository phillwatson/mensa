package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.auditor.domain.Payout;
import com.hillayes.mensa.auditor.repository.delta.DeltaStrategy;
import com.hillayes.mensa.events.domain.EventPacket;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
@Slf4j
public class PayoutMongoRepository extends BaseMongoRepository<Payout> {
    public boolean updateDelta(DeltaStrategy deltaStrategy, EventPacket event) {
        return deltaStrategy.mongoDelta(mongoCollection().getCodecRegistry(), event)
            .map(updateDoc -> {
                // use upsert to create new or update existing record
                UpdateResult updateResult = mongoCollection()
                    .updateOne(
                        eq("_id", deltaStrategy.getEntityId(event)),
                        updateDoc,
                        new UpdateOptions().upsert(true)
                    );

                if (log.isTraceEnabled()) {
                    log.trace("Updated payout doc [matched: {}, modified: {}, upsertedId: {}]",
                        updateResult.getMatchedCount(), updateResult.getModifiedCount(), updateResult.getUpsertedId());
                }
                return true;
            })
            .orElse(false);
    }
}
