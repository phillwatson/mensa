package com.hillayes.mensa.outbox.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hillayes.mensa.events.domain.EventPacket;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class EventPacketSerializer extends ObjectMapperSerializer<EventPacket> {
    public EventPacketSerializer() {
        super();
    }

    public EventPacketSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
