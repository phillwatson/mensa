package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.config.ConsumerBean;
import com.hillayes.mensa.events.domain.EventPacket;
import io.quarkus.runtime.ShutdownEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
@Slf4j
public class ConsumerFactory {
    private final Properties consumerConfig;

    private Consumer<String, EventPacket> consumer;
    private final Map<String, TopicListener> listeners = new HashMap<>();
    private final AtomicBoolean listenersAdded = new AtomicBoolean();

    private CountDownLatch shutdownLatch;

    public ConsumerFactory(@ConsumerBean Properties consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public void addTopicListener(TopicListener listener) {
        log.info("Adding message listener [topic: {}]", listener.getTopic());

        synchronized (listeners) {
            listeners.put(listener.getTopic().topicName(), listener);
            listenersAdded.set(true);
        }

        ensureConsumerStarted();
    }

    void onStop(@Observes ShutdownEvent ev) throws InterruptedException {
        if (consumer != null) {
            log.info("Shutting down Consumer - started");

            synchronized (listeners) {
                commitAcknowledgements();
                listeners.forEach((topic, listener) -> listener.stop());
                listeners.clear();
            }

            consumer.wakeup();
            boolean success = shutdownLatch.await(1, TimeUnit.MINUTES);
            log.info("Shutting down Consumer - complete [success: {}]", success);
        }
    }

    private void ensureConsumerStarted() {
        if (consumer != null) {
            return;
        }

        log.info("Starting consumer [clientId: {}, groupId: {}]",
            consumerConfig.getProperty(ConsumerConfig.CLIENT_ID_CONFIG),
            consumerConfig.getProperty(ConsumerConfig.GROUP_ID_CONFIG));

        consumer = new KafkaConsumer<>(consumerConfig);
        new Thread(() -> {
            shutdownLatch = new CountDownLatch(1);
            try {
                while (true) {
                    if (listenersAdded.getAndSet(false)) {
                        Collection<String> topics = listeners.keySet();
                        log.debug("Subscribing to topics: {}", topics);
                        consumer.subscribe(topics);
                    }

                    commitAcknowledgements();

                    log.trace("Polling for EventPackets");
                    process(consumer.poll(Duration.ofMillis(1000)));
                }
            } catch (WakeupException e) {
                // ignore, we're closing
            } catch (Exception e) {
                log.error("Stopping Kafka Consumer due to unexpected error.", e);
            } finally {
                log.info("Closing consumer.");
                consumer.close();
                consumer = null;
                shutdownLatch.countDown();
            }
        }, "event-poller").start();
    }

    private void commitAcknowledgements() {
        Map<TopicPartition, OffsetAndMetadata> commitments = new HashMap<>();
        synchronized (listeners) {
            listeners.values().forEach(listener ->
                commitments.putAll(listener.pendingCommits())
            );
        }

        if (!commitments.isEmpty()) {
            log.debug("Issuing offset commitments: {}", commitments);
            consumer.commitSync(commitments);
            log.trace("Issued offset commitments: {}", commitments);
        }
    }

    private void process(ConsumerRecords<String, EventPacket> records) {
        if (!records.isEmpty()) {
            log.debug("Received Consumer Records [count: {}]", records.count());
            records.partitions().forEach(partition -> process(partition, records.records(partition)));
        }
    }

    private void process(TopicPartition topicPartition,
                         List<ConsumerRecord<String, EventPacket>> records) {
        log.debug("Processing Consumer Records [topicPartition: {}, count: {}]",
            topicPartition, records.size());

        TopicListener listener = listeners.get(topicPartition.topic());
        if (listener != null) {
            log.trace("Found listener [{}]", listener.getClass().getName());
            listener.process(topicPartition, records);
        }
    }
}
