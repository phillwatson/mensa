package com.hillayes.mensa.outbox.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.exceptions.EventPayloadDeserializationException;
import com.hillayes.mensa.events.exceptions.EventPayloadSerializationException;
import com.hillayes.mensa.executors.correlation.Correlation;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="events")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventPacketEntity implements EventPacket {
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
    @Setter
    private Instant deliveredAt;

    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(name="payload_class")
    private String payloadClass;

    private String payload;

    @JsonIgnore
    @Transient
    private Object payloadContent;

    public EventPacketEntity(Topic topic, Object payloadObject) {
        this(topic, payloadObject, Correlation.getCorrelationId().orElse(UUID.randomUUID().toString()), Instant.now());
    }

    public EventPacketEntity(Topic topic, Object payloadObject, String correlationId, Instant timestamp) {
        this.correlationId = correlationId;
        this.timestamp = timestamp;
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
