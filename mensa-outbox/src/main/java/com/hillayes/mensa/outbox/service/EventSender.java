package com.hillayes.mensa.outbox.service;

import com.hillayes.mensa.outbox.domain.EventPacket;
import com.hillayes.mensa.outbox.domain.Topic;
import com.hillayes.mensa.outbox.repository.EventPacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EventSender {
    private final EventPacketRepository eventPacketRepository;

    public <T> void send(Topic topic, T event) {
        log.debug("Sending event [payload: {}]", event.getClass().getName());
        EventPacket packet = new EventPacket(topic, event);
        eventPacketRepository.persist(packet);
    }
}
