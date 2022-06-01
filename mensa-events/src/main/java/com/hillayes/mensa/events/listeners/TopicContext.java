package com.hillayes.mensa.events.listeners;

import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Message;
import com.hillayes.mensa.events.domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class TopicContext {
    @Getter
    private final Topic topic;

    private final ProcessorStrategy processor;
    private final AcknowledgementStrategy acknowledgementStrategy;
    private final FailureStrategy failureStrategy;

    private final Map<TopicPartition, OffsetAndMetadata> pendingCommits = new HashMap<>();

    @Getter
    @Setter(AccessLevel.MODULE)
    private Consumer<String, EventPacket> consumer;

    private CountDownLatch shutdownLatch;

    public void stop() throws InterruptedException {
        if (consumer != null) {
            log.info("Shutting down Message Consumer [topic: {}]", topic);
            shutdownLatch = new CountDownLatch(1);
            consumer.wakeup();

            boolean success = shutdownLatch.await(1, TimeUnit.MINUTES);

            processor.stop(this);
            acknowledgementStrategy.stop(this);
            failureStrategy.stop(this);
            log.info("Shutting down Message Consumer [topic: {}, result: {}]", topic, success);
        }
    }

    protected void stopped() {
        commitPending();
        consumer.close();
        consumer = null;
        shutdownLatch.countDown();
    }

    protected ConsumerRecords<String, EventPacket> poll(Duration duration) {
        commitPending();
        return consumer.poll(duration);
    }

    protected void process(ConsumerRecords<String, EventPacket> records) {
        commitPending();

        log.trace("Received Consumer Records [count: {}]", records.count());
        records.partitions().forEach(partition ->
            records.records(partition).forEach(record -> process(partition, record))
        );
    }

    private void process(TopicPartition topicPartition, ConsumerRecord<String, EventPacket> record) {
        final TopicContext context = this;
        Message message = new Message(topicPartition, record) {
            public void ack() {
                acknowledgementStrategy.ack(context, this);
            }

            public void nack() {
                failureStrategy.failure(context, this);
            }

            public void nack(Throwable cause) {
                failureStrategy.failure(context, this, cause);
            }
        };

        try {
            acknowledgementStrategy.received(this, message);
            processor.process(this, message);
        } catch (Exception e) {
            failureStrategy.failure(this, message, e);
        }
    }

    public void commit(Map<TopicPartition, OffsetAndMetadata> newCommits) {
        synchronized (pendingCommits) {
            log.debug("Queuing offset commits: {}", newCommits);
            pendingCommits.putAll(newCommits);
        }
    }

    private void commitPending() {
        synchronized (pendingCommits) {
            if ((consumer != null) && (!pendingCommits.isEmpty())) {
                log.debug("Committing pending offsets: {}", pendingCommits);
                try {
                    consumer.commitSync(pendingCommits);
                    pendingCommits.clear();
                    log.debug("Committed pending offsets");
                } catch (Exception e) {
                    log.error("Failed to commit offset.", e);
                }
            }
        }
    }
}
