package com.hillayes.mensa.outbox.sender;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.outbox.repository.EventEntity;
import com.hillayes.mensa.outbox.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EventSender {
    private final EventRepository eventRepository;

    public <T> void send(Topic topic, T event) {
        log.debug("Sending event [payload: {}]", event.getClass().getName());
        eventRepository.persist(new EventEntity(topic, event));
    }

    public <T> void send(Topic topic, T event, String correlationId, Instant timestamp) {
        log.debug("Sending event [payload: {}]", event.getClass().getName());
        eventRepository.persist(new EventEntity(topic, event, correlationId, timestamp));
    }
}
