package com.hillayes.mensa.auditor.repository;

import com.hillayes.mensa.auditor.domain.Payment;
import com.hillayes.mensa.auditor.repository.delta.DeltaStrategy;
import com.hillayes.mensa.events.domain.EventPacket;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.WriteModel;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Slf4j
public class PaymentMongoRepository extends BaseMongoRepository<Payment> {
    @Transactional
    public boolean updateDelta(DeltaStrategy deltaStrategy, EventPacket event) {
        List<WriteModel<? extends Payment>> update = deltaStrategy.mongoDelta(mongoCollection().getCodecRegistry(), event);
        if ((update == null) || (update.isEmpty())) {
            log.debug("Payment event no-op update [deltaStrategy: {}, eventPayload: {}]",
                deltaStrategy.getClass().getName(), event.getPayload());
            return false;
        }

        log.trace("Updating payment [size: {}]", update.size());

        // submit all updates in bulk
        BulkWriteResult updateResult = mongoCollection().bulkWrite(update);

        if (log.isTraceEnabled()) {
            log.trace("Updated payment doc [matched: {}, modified: {}, deleted: {}, inserted: {}, ack: {}]",
                updateResult.getMatchedCount(), updateResult.getModifiedCount(),
                updateResult.getDeletedCount(), updateResult.getInsertedCount(),
                updateResult.wasAcknowledged());
        }
        return true;
    }
}
