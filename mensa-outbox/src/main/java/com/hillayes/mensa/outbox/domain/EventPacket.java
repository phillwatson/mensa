package com.hillayes.mensa.outbox.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hillayes.mensa.executors.correlation.Correlation;
import com.hillayes.mensa.outbox.exceptions.EventPayloadDeserializationException;
import com.hillayes.mensa.outbox.exceptions.EventPayloadSerializationException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.Instant;
import java.util.UUID;

@Entity(name="events")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventPacket {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Id
    @GeneratedValue(generator = "uuid2")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name="correlation_id")
    private String correlationId;

    @Column(name="timestamp")
    private Instant timestamp;

    @Column(name="delivered_at")
    private Instant deliveredAt;

    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(name="payload_class")
    private String payloadClass;

    private String payload;

    @JsonIgnore
    @Transient
    private Object payloadContent;

    public EventPacket(Topic topic, Object payloadObject) {
        correlationId = Correlation.getCorrelationId().orElse(UUID.randomUUID().toString());
        timestamp = Instant.now();
        this.topic = topic;

        try {
            payloadClass = payloadObject.getClass().getName();
            payload = MAPPER.writeValueAsString(payloadObject);
        } catch (JsonProcessingException e) {
            throw new EventPayloadSerializationException(payloadClass, e);
        }
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
