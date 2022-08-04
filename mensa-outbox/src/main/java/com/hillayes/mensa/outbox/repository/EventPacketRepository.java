package com.hillayes.mensa.outbox.repository;

import com.hillayes.mensa.outbox.domain.EventPacket;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.stream.Stream;

@ApplicationScoped
@NamedQueries({
        @NamedQuery(name="EventPacket.undelivered", query = "FROM EventPacket WHERE deliveredAt IS NULL ORDER BY postedAt LIMIT :batchSize FOR UPDATE")
})
public class EventPacketRepository implements PanacheRepository<EventPacket> {
    /**
     * Returns the undelivered events in the order they were posted. The events are locked to prevent
     * allow update and prevent update conflicts.
     *
     * @param batchSize the max number of events to be returned.
     * @return a stream of undelivered events.
     */
    public Stream<EventPacket> listUndelivered(int batchSize) {
        return find("#EventPacket.undelivered", Parameters.with("batchSize", batchSize))
                .page(Page.ofSize(batchSize))
                .firstPage()
                .stream();
    }
}
