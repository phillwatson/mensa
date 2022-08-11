package com.hillayes.mensa.outbox.service;

import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.outbox.repository.EventPacketEntity;
import com.hillayes.mensa.outbox.repository.EventPacketRepository;
import com.hillayes.mensa.outbox.sender.ProducerFactory;
import io.quarkus.scheduler.Scheduled;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EventDeliverer {
    private final ProducerFactory producerFactory;
    private final EventPacketRepository eventPacketRepository;

    /**
     * A scheduled service to read pending events from the event outbox table and
     * send them to the message broker.
     */
    @Scheduled(cron = "${mense.events.cron:1/5 * * * * ?}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    @Transactional
    public void deliverEvents() {
        log.trace("Event delivery started");
        List<EventPacketEntity> events = eventPacketRepository.listUndelivered(50);
        log.trace("Found events for delivery [size: {}]", events.size());

        List<EventRecord> records = events.stream()
                .map(packet -> send(packet.getTopic(), null, packet))
                .collect(Collectors.toList());

        log.trace("Delivered events [size: {}]", records.size());
        records.stream()
                .filter(record -> !record.isError())
                .forEach(record -> {
                    EventPacketEntity packet = record.getEventPacket();
                    packet.setDeliveredAt(Instant.now());
                    eventPacketRepository.persist(packet);
                });

        // wait for events to complete
        log.trace("Event delivery complete");
    }

    private EventRecord send(Topic topic, String key, EventPacketEntity eventPacket) {
        EventRecord.EventRecordBuilder result =
                EventRecord.builder().eventPacket(eventPacket);

        try {
            log.trace("Delivering event [id: {}, topic: {}, payload: {}]",
                    eventPacket.getId(), eventPacket.getTopic(), eventPacket.getPayloadClass());

            ProducerRecord<String, EventPacket> record = new ProducerRecord<>(topic.topicName(), key, eventPacket);
            producerFactory.getProducer().send(record);
        } catch (Exception e) {
            log.warn("Failed to deliver event [id: {}, topic: {}, payload: {}]",
                    eventPacket.getId(), eventPacket.getTopic(), eventPacket.getPayloadClass());
            result.error(true);
        }

        return result.build();
    }

    @Builder
    @Getter
    private static class EventRecord {
        private EventPacketEntity eventPacket;
        private Future<RecordMetadata> eventResponse;
        private boolean error;
    }
}
