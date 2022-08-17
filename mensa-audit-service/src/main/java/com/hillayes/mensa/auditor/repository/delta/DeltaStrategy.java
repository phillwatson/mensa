package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.events.domain.EventPacket;
import com.mongodb.client.model.WriteModel;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;

import java.util.List;
import java.util.Optional;

public interface DeltaStrategy {
    public boolean isApplicable(String payloadClass);

    /**
     * The implementation will, if appropriate, return a Neo4J Cypher update query to
     * update the payment graph with the properties of the given event object.
     *
     * @param event the event object to be reflected in the payment graph.
     * @return the optional query to be executed to update the payment graph.
     */
    public abstract Optional<Query> neoDelta(EventPacket event);

    /**
     * The implementation will, if appropriate, return a MongoDB update query to
     * update the payment document with the properties of the given event object.
     *
     * @param event the event object to be reflected in the payment document.
     * @return the optional query to be executed to update the payment document.
     */
    public abstract <T> List<WriteModel<? extends T>> mongoDelta(CodecRegistry codecRegistry, EventPacket event);
}
