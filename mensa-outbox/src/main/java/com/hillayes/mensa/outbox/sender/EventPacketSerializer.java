package com.hillayes.mensa.outbox.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hillayes.mensa.outbox.domain.EventPacket;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class EventPacketSerializer extends ObjectMapperSerializer<EventPacket> {
    public EventPacketSerializer() {
        super();
    }

    public EventPacketSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
