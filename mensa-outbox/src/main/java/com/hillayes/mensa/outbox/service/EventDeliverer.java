package com.hillayes.mensa.outbox.service;

import com.hillayes.mensa.outbox.domain.EventPacket;
import com.hillayes.mensa.outbox.domain.Topic;
import com.hillayes.mensa.outbox.repository.EventPacketRepository;
import com.hillayes.mensa.outbox.sender.ProducerFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EventDeliverer {
    private final ProducerFactory producerFactory;
    private final EventPacketRepository eventPacketRepository;

    public void deliverEvents() {
        // post events to broker
        List<EventRecord> records = eventPacketRepository
                .listUndelivered(50)
                .map(packet -> send(packet.getTopic(), null, packet))
                .collect(Collectors.toList());

        // wait for events to complete
    }

    private EventRecord send(Topic topic, String key, EventPacket eventPacket) {
        EventRecord.EventRecordBuilder result =
                EventRecord.builder().eventPacket(eventPacket);

        try {
            ProducerRecord<String, EventPacket> record = new ProducerRecord<>(topic.topicName(), key, eventPacket);
            producerFactory.getProducer().send(record);
        } catch (Exception e) {
            result.error(true);
        }

        return result.build();
    }

    @Builder
    @Getter
    private static class EventRecord {
        private EventPacket eventPacket;
        private Future<RecordMetadata> eventResponse;
        private boolean error;
    }
}
