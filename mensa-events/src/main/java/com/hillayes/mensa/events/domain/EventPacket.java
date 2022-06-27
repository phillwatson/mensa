package com.hillayes.mensa.events.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hillayes.mensa.events.exceptions.EventPayloadDeserializationException;
import com.hillayes.mensa.events.exceptions.EventPayloadSerializationException;
import com.hillayes.mensa.executors.correlation.Correlation;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@ToString(exclude = "payload")
public class EventPacket {
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private String correlationId;
    private Instant timestamp;
    private String payloadClass;
    private String payload;

    @JsonIgnore
    private Object payloadContent;

    protected EventPacket() {
    }

    public EventPacket(Object payloadObject) {
        correlationId = Correlation.getCorrelationId().orElse(UUID.randomUUID().toString());
        timestamp = Instant.now();
        payloadClass = payloadObject.getClass().getName();

        try {
            payload = MAPPER.writeValueAsString(payloadObject);
        } catch (JsonProcessingException e) {
            throw new EventPayloadSerializationException(payloadClass, e);
        }
    }

    public EventPacket(String payloadClass, String payload) {
        correlationId = Correlation.getCorrelationId().orElse(UUID.randomUUID().toString());
        timestamp = Instant.now();
        this.payloadClass = payloadClass;
        this.payload = payload;
    }

    @JsonIgnore
    public <T> T getPayloadContent() {
        if (payloadContent == null) {
            try {
                payloadContent = MAPPER.readValue(payload, Class.forName(payloadClass));
            } catch (JsonProcessingException | ClassNotFoundException e) {
                throw new EventPayloadDeserializationException(payloadClass, e);
            }
        }
        return (T) payloadContent;
    }
}
