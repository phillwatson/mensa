package com.hillayes.mensa.events.sender;

import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Future;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class EventSender {
    private final ProducerFactory producerFactory;

    public <T> Future<RecordMetadata> send(Topic topic, T event)  {
        log.debug("Sending event [topic: {}, class: {}]", topic, event.getClass().getName());

        EventPacket eventPacket = new EventPacket(event);
        return send(topic, eventPacket);
    }

    public Future<RecordMetadata> send(Topic topic, EventPacket value) {
        return send(topic, null, value);
    }

    public Future<RecordMetadata> send(Topic topic, String key, EventPacket value) {
        ProducerRecord<String, EventPacket> record = new ProducerRecord<>(topic.topicName(), key, value);
        return producerFactory.getProducer().send(record);
    }
}
