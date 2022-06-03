package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class PeriodicAcknowledgement implements AcknowledgementStrategy {
    private static final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1);

    private final Duration period;
    private final Map<TopicPartition, List<Long>> pending = new HashMap<>();
    private final Map<TopicPartition, Set<Long>> acked = new HashMap<>();
    private ScheduledFuture<?> schedule;

    public PeriodicAcknowledgement(Duration period) {
        this.period = period;
    }

    @Override
    public boolean stop(TopicListener topicListener) {
        log.debug("Stopping PeriodicAcknowledgement [topic: {}]", topicListener.getTopic());
        boolean result = (schedule == null) || (schedule.cancel(false));

        log.trace("Stopped PeriodicAcknowledgement [topic: {}, result: {}]", topicListener.getTopic(), result);
        return result;
    }

    @Override
    public void received(TopicListener topicListener, Message message) {
        log.debug("Acknowledgement pending [topic-partition: {}, offset: {}]",
            message.getTopicPartition(), message.getRecord().offset());

        TopicPartition partition = message.getTopicPartition();
        long offset = message.getRecord().offset();
        synchronized (pending) {
            pending.computeIfAbsent(partition, topic -> new ArrayList<>()).add(offset);
        }
    }

    @Override
    public void ack(TopicListener topicListener, Message message) {
        log.debug("Acknowledgement received [topic-partition: {}, offset: {}]",
            message.getTopicPartition(), message.getRecord().offset());

        TopicPartition partition = message.getTopicPartition();
        long offset = message.getRecord().offset();
        synchronized (pending) {
            acked.computeIfAbsent(partition, topic -> new HashSet<>()).add(offset);

            if (schedule == null) {
                schedule(topicListener);
            }
        }
    }

    private void schedule(final TopicListener topicListener) {
        log.debug("Adding acknowledgment schedule");

        schedule = scheduler.scheduleAtFixedRate(() -> {
            log.trace("Scheduled acknowledgement running [topic: {}]", topicListener.getTopic());
            final Map<TopicPartition, OffsetAndMetadata> queued = new HashMap<>();

            synchronized (pending) {
                acked.forEach((partition, offsets) -> {
                    AtomicLong offset = new AtomicLong(-1L);
                    List<Long> outstanding = pending.computeIfPresent(partition, (k, v) -> v.stream()
                        .sorted()
                        .dropWhile(n -> offsets.contains(n) && offset.updateAndGet(l -> (l < 0 || l + 1 == n) ? n : l) == n)
                        .collect(Collectors.toList())
                    );

                    if ((offset.get() >= 0) && (outstanding != null)) {
                        pending.put(partition, outstanding);
                        offsets.retainAll(outstanding);

                        queued.put(partition, new OffsetAndMetadata(offset.get() + 1));
                    }
                });
            }

            if (!queued.isEmpty()) {
                log.debug("Posting offset commitments: {}", queued);
                topicListener.commit(queued);
                log.trace("Posted offset commitments");
            }
        }, period.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
    }
}
