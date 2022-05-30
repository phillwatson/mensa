package com.hillayes.mensa.events.reciever;

import com.hillayes.mensa.events.config.ConsumerBean;
import com.hillayes.mensa.events.domain.EventListener;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.executors.ExecutorConfiguration;
import com.hillayes.mensa.executors.ExecutorFactory;
import com.hillayes.mensa.executors.ExecutorType;
import com.hillayes.mensa.executors.correlation.Correlation;
import io.quarkus.runtime.ShutdownEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Slf4j
public class ConsumerFactory {
    @ConfigProperty(name="mensa.events.listener-threads", defaultValue = "10")
    private int executorThreadCount;
    private final Properties consumerConfig;

    private Consumer<String, EventPacket> consumer;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private final Map<String, EventListener> listeners = new HashMap<>();
    private ExecutorService topicExecutor;

    public ConsumerFactory(@ConsumerBean Properties consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public void addListener(@NotNull Topic topic, @NotNull EventListener listener) {
        log.info("Adding message listener [topic: {}]", topic);
        if (shutdownLatch.getCount() <= 0) {
            throw new IllegalStateException("Consumer cannot be restarted after shutdown.");
        }

        ensureConsumerStarted();
        listeners.put(topic.topicName(), listener);
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
            try {
                Collection<String> topics = Topic.allTopicNames();
                log.info("Subscribing to topics: {}", topics);
                consumer.subscribe(topics);

                while (true) {
                    log.trace("Polling for EventPackets");
                    ConsumerRecords<String, EventPacket> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));

                    log.trace("Received Consumer Records [count: {}]", records.count());

                    records.partitions().forEach(partition -> {
                        records.records(partition).forEach(
                            this::process
                        );
                    });
                    // records.forEach(this::process);
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

    void onStop(@Observes ShutdownEvent ev) throws InterruptedException {
        if (consumer != null) {
            if (topicExecutor != null) {
                log.info("Shutting down Message Executors - started");
                topicExecutor.shutdown();
                boolean success = topicExecutor.awaitTermination(1, TimeUnit.MINUTES);
                log.info("Shutting down Message Executors - complete [success: {}]", success);
            }

            log.info("Shutting down Message Consumers - started");
            consumer.wakeup();
            boolean success = shutdownLatch.await(1, TimeUnit.MINUTES);
            log.info("Shutting down Message Consumers - complete [success: {}]", success);
        }
    }

    private void process(ConsumerRecord<String, EventPacket> record) {
        if (log.isDebugEnabled()) {
            log.debug("Processing EventPacket [topic: {}, payloadClass: {}, correlationId: {}]",
                record.topic(), record.value().getPayloadClass(), record.value().getCorrelationId());
        }

        EventListener listener = listeners.get(record.topic());
        if (listener != null) {
            log.trace("Found listener [{}]", listener.getClass().getName());

            if (topicExecutor == null) {
                topicExecutor = ExecutorFactory.newExecutor(ExecutorConfiguration.builder()
                    .name("event-executor")
                    .executorType(ExecutorType.CACHED)
                    .numberOfThreads(executorThreadCount)
                    .build());
            }

            if (log.isTraceEnabled()) {
                log.trace("Submitting event for processing [topic: {}, payloadClass: {}, correlationId: {}]",
                    record.topic(), record.value().getPayloadClass(), record.value().getCorrelationId());
            }

            topicExecutor.submit(() -> {
                try {
                    Correlation.call(record.value().getCorrelationId(), listener, record.value());
                    // acknowledge the event
                } catch (Exception e) {
                    log.error("Event processing, internal failure:", e);
                    // fail the event
                }
            });
        }
    }
}
