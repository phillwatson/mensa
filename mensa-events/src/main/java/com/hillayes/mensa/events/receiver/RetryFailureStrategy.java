package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryFailureStrategy implements FailureStrategy {
    public RetryFailureStrategy(int retryCount) {
    }

    @Override
    public void failure(TopicListener topicListener, Message message) {
        if (log.isDebugEnabled()) {
            log.debug("Message explicitly failed [topic-partition: {}, offset: {}, eventPacket: {}]",
                message.getTopicPartition(), message.getRecord().offset(),
                message.getRecord().value());
        }
    }

    @Override
    public void failure(TopicListener topicListener, Message message, Throwable cause) {
        log.error("Message implicitly failed [topic-partition: {}, offset: {}, eventPacket: {}]",
            message.getTopicPartition(), message.getRecord().offset(),
            message.getRecord().value());
        log.error("Message implicitly failed", cause);
    }

    @Override
    public boolean stop(TopicListener topicListener) {
        log.debug("Stopped RetryFailureStrategy [topic: {}]", topicListener.getTopic());
        boolean result = true;
        log.trace("Stopped RetryFailureStrategy [topic: {}, result: {}]", topicListener.getTopic(), result);
        return result;
    }
}
