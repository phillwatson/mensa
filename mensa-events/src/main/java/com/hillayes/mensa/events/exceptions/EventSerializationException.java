package com.hillayes.mensa.events.exceptions;

import com.hillayes.mensa.events.domain.EventPacket;

public class EventSerializationException extends RuntimeException {
    public EventSerializationException(EventPacket eventPacket, Throwable cause) {
        super(String.format("Failed to serialize event packet [payloadClass: %1$s]",
            eventPacket.getPayloadClass()), cause);
    }
}
