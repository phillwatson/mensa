package com.hillayes.mensa.outbox.sender;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.outbox.repository.EventPacketEntity;
import com.hillayes.mensa.outbox.repository.EventPacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EventSender {
    private final EventPacketRepository eventPacketRepository;

    public <T> void send(Topic topic, T event) {
        log.debug("Sending event [payload: {}]", event.getClass().getName());
        eventPacketRepository.persist(new EventPacketEntity(topic, event));
    }

    public <T> void send(Topic topic, T event, String correlationId, Instant timestamp) {
        log.debug("Sending event [payload: {}]", event.getClass().getName());
        eventPacketRepository.persist(new EventPacketEntity(topic, event, correlationId, timestamp));
    }
}
