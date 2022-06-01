package com.hillayes.mensa.events.listeners;

import com.hillayes.mensa.events.domain.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class TopicConsumer {
    private final TopicContext topicContext;

    public TopicConsumer(Topic topic,
                         Properties consumerConfig,
                         AcknowledgementStrategy acknowledgementStrategy,
                         FailureStrategy failureStrategy,
                         ProcessorStrategy processor) {
        this.topicContext = new TopicContext(topic, processor, acknowledgementStrategy, failureStrategy);
        start(consumerConfig);
    }

    private void start(Properties consumerConfig) {
        if (topicContext.getConsumer() != null) {
            return;
        }

        log.info("Starting consumer [clientId: {}, groupId: {}]",
            consumerConfig.getProperty(ConsumerConfig.CLIENT_ID_CONFIG),
            consumerConfig.getProperty(ConsumerConfig.GROUP_ID_CONFIG));

        topicContext.setConsumer(new KafkaConsumer<>(consumerConfig));
        new Thread(() -> {
            try {
                log.info("Subscribing to topic: {}", topicContext.getTopic());
                topicContext.getConsumer().subscribe(Collections.singleton(topicContext.getTopic().topicName()));

                while (true) {
                    log.trace("Polling for EventPackets");
                    topicContext.process(topicContext.poll(Duration.ofMillis(1000)));
                }
            } catch (WakeupException e) {
                // ignore, we're closing
            } catch (Exception e) {
                log.error("Stopping Kafka Consumer due to unexpected error.", e);
            } finally {
                topicContext.stopped();
            }
        }, topicContext.getTopic() + "-event-poller").start();
    }

    public void stop() throws InterruptedException {
        topicContext.stop();
    }
}
