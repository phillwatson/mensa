package com.hillayes.mensa.events.domain;

import java.time.Instant;
import java.util.UUID;

public interface EventPacket {
    public String getCorrelationId();

    public Instant getTimestamp();

    public Topic getTopic();

    public String getPayloadClass();

    public String getPayload();

    public <T> T getPayloadContent();
}
