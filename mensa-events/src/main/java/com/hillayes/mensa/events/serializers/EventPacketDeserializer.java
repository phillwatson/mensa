package com.hillayes.mensa.events.serializers;

import com.hillayes.mensa.events.domain.EventPacket;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class EventPacketDeserializer extends ObjectMapperDeserializer<EventPacket> {
    public EventPacketDeserializer() {
        super(EventPacket.class);
    }
}
