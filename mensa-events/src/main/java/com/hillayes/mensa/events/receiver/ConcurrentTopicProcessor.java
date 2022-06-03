package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.EventListener;
import com.hillayes.mensa.events.domain.EventPacket;
import com.hillayes.mensa.events.domain.Message;
import com.hillayes.mensa.executors.ExecutorConfiguration;
import com.hillayes.mensa.executors.ExecutorFactory;
import com.hillayes.mensa.executors.ExecutorType;
import com.hillayes.mensa.executors.correlation.Correlation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConcurrentTopicProcessor implements ProcessorStrategy {
    private final EventListener listener;
    private final int threadCount;
    private ExecutorService executorService;

    public ConcurrentTopicProcessor(EventListener listener,
                                    int threadCount) {
        this.listener = listener;
        this.threadCount = threadCount;
    }

    @Override
    public void process(TopicListener topicListener, final Message message) {
        if (executorService == null) {
            String topic = message.getTopicPartition().topic();
            executorService = ExecutorFactory.newExecutor(ExecutorConfiguration.builder()
                .name(topic + "-event-executor")
                .executorType(ExecutorType.CACHED)
                .numberOfThreads(threadCount)
                .build());
        }

        final ConsumerRecord<String, EventPacket> record = message.getRecord();
        if (log.isDebugEnabled()) {
            log.trace("Submitting event for processing [topic: {}, partition: {}, offset: {}, payloadClass: {}, correlationId: {}]",
                record.topic(), record.partition(), record.offset(),
                record.value().getPayloadClass(), record.value().getCorrelationId());
        }

        executorService.submit(() -> {
            try {
                if (log.isDebugEnabled()) {
                    log.trace("Processing event [topic: {}, partition: {}, offset: {}, payloadClass: {}, correlationId: {}]",
                        record.topic(), record.partition(), record.offset(),
                        record.value().getPayloadClass(), record.value().getCorrelationId());
                }

                Correlation.call(record.value().getCorrelationId(), listener, record.value());
                message.ack();
            } catch (Exception e) {
                log.error("Event processing, internal failure:", e);
                message.nack(e);
            }
        });
    }

    @Override
    public boolean stop(TopicListener topicListener) {
        log.debug("Shutting down processor [topic: {}]", topicListener.getTopic());
        if (executorService != null) {
            executorService.shutdown();
        }

        boolean result = false;
        try {
            result = (executorService == null) || (executorService.awaitTermination(20, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.trace("Shutting down processor [topic: {}, result: {}]", topicListener.getTopic(), result);
        return result;
    }
}
