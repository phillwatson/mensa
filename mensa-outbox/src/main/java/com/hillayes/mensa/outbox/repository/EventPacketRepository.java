package com.hillayes.mensa.outbox.repository;

import com.hillayes.mensa.events.domain.EventPacket;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.LockModeType;
import java.util.List;

@ApplicationScoped
public class EventPacketRepository implements PanacheRepository<EventPacketEntity> {
    /**
     * Returns the undelivered events in the order they were posted. The events are locked to prevent
     * allow update and prevent update conflicts.
     *
     * @param batchSize the max number of events to be returned.
     * @return a stream of undelivered events.
     */
    public List<EventPacketEntity> listUndelivered(int batchSize) {
        return find("FROM EventPacket WHERE deliveredAt IS NULL ORDER BY timestamp")
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .page(0, batchSize)
                .list();
    }
}
