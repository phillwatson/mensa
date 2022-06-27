package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.events.domain.EventPacket;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.neo4j.driver.Query;

import java.util.Optional;
import java.util.UUID;

public abstract class DeltaStrategy {
    private String forEventClass;

    protected DeltaStrategy(Class<?> forEventClass) {
        this.forEventClass = forEventClass.getName();
    }

    public boolean isApplicable(String payloadClass) {
        return forEventClass.equalsIgnoreCase(payloadClass);
    }

    /**
     * Returns the identifier by which the entity to be updated can be identified.
     * For example, if the event identifies a payout then the ID of that payout
     * will be returned.
     *
     * @param event the event object containing the payload which carries the ID.
     * @return the unique identifier for the entity to be updated.
     */
    public abstract UUID getEntityId(EventPacket event);

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
    public abstract Optional<BsonDocument> mongoDelta(CodecRegistry codecRegistry, EventPacket event);


    protected <T> BsonDocument encode(CodecRegistry codecRegistry, T instance) {
        Codec<T> codec = (Codec<T>) codecRegistry.get(instance.getClass());

        BsonDocument result = new BsonDocument();
        codec.encode(new BsonDocumentWriter(result), instance, EncoderContext.builder().build());

        return result;
    }
}
