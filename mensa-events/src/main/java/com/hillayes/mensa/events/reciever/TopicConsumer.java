package com.hillayes.mensa.events.reciever;

import com.hillayes.mensa.events.domain.EventListener;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.executors.ExecutorConfiguration;
import com.hillayes.mensa.executors.ExecutorFactory;
import com.hillayes.mensa.executors.ExecutorType;
import com.hillayes.mensa.executors.correlation.Correlation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TopicConsumer {
    private final EventListener listener;
    private final Acknowledgement acknowledgement;
    private ExecutorService topicExecutor;

    public TopicConsumer(EventListener aListener, Acknowledgement aAcknowledgement) {
        listener = aListener;
        acknowledgement = aAcknowledgement;
    }

    public void process(TopicPartition aTopicPartition, Collection<ConsumerRecord<String, EventPacket>> aRecords) {
        ExecutorService executor = getTopicExecutor(aTopicPartition.topic());
        aRecords.forEach(record ->
            executor.submit(() -> {
                try {
                    Correlation.call(record.value().getCorrelationId(), listener, record.value());
                    acknowledgement.ack(record);
                } catch (Exception e) {
                    log.error("Event processing, internal failure:", e);
                    // fail the event
                }
            })
        );
    }

    public boolean stop() throws InterruptedException {
        if (topicExecutor == null) {
            return true;
        }
        ExecutorService executor = topicExecutor;
        topicExecutor = null;

        executor.shutdown();
        return executor.awaitTermination(1, TimeUnit.MINUTES);
    }

    private ExecutorService getTopicExecutor(String aTopic) {
        if (topicExecutor == null) {
            topicExecutor = ExecutorFactory.newExecutor(ExecutorConfiguration.builder()
                .name(aTopic + "-executor")
                .executorType(ExecutorType.CACHED)
                .numberOfThreads(10)
                .build());
        }

        return topicExecutor;
    }
}
