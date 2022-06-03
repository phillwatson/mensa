package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Message;
import com.hillayes.mensa.events.domain.Topic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Slf4j
public class TopicListener {
    private final Topic topic;
    private final ProcessorStrategy processor;
    private final AcknowledgementStrategy acknowledgementStrategy;
    private final FailureStrategy failureStrategy;

    private final Object commitmentLock = new Object();
    private Map<TopicPartition, OffsetAndMetadata> pendingCommits = new HashMap<>();

    public void stop() {
        log.debug("Shutting down [topic: {}]", topic);
        processor.stop(this);
        acknowledgementStrategy.stop(this);
        failureStrategy.stop(this);
        log.trace("Shut down [topic: {}]", topic);
    }

    public void commit(Map<TopicPartition, OffsetAndMetadata> newCommits) {
        synchronized (commitmentLock) {
            log.debug("Queuing offset commits: {}", newCommits);
            pendingCommits.putAll(newCommits);
        }
    }

    public Map<TopicPartition, OffsetAndMetadata> acknowledgements() {
        synchronized (commitmentLock) {
            Map<TopicPartition, OffsetAndMetadata> result = pendingCommits;
            pendingCommits = new HashMap<>();
            return result;
        }
    }

    public void process(TopicPartition topicPartition,
                        List<ConsumerRecord<String, EventPacket>> records) {
        records.forEach(record -> process(topicPartition, record));
    }

    private void process(TopicPartition topicPartition, ConsumerRecord<String, EventPacket> record) {
        final TopicListener thisListener = this;
        Message message = new Message(topicPartition, record) {
            public void ack() { acknowledgementStrategy.ack(thisListener, this); }
            public void nack() { failureStrategy.failure(thisListener, this); }
            public void nack(Throwable cause) { failureStrategy.failure(thisListener, this, cause); }
        };

        try {
            acknowledgementStrategy.received(this, message);
            processor.process(this, message);
        } catch (Exception e) {
            failureStrategy.failure(this, message, e);
        }
    }
}
