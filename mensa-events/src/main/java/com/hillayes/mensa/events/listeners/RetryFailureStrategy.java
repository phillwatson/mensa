package com.hillayes.mensa.events.listeners;

import com.hillayes.mensa.events.domain.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryFailureStrategy implements FailureStrategy {
    public RetryFailureStrategy(int retryCount) {

    }

    @Override
    public void failure(TopicContext topicContext, Message message) {
        if (log.isDebugEnabled()) {
            log.debug("Message explicitly failed [topic-partition: {}, offset: {}, eventPacket: {}]",
                message.getTopicPartition(), message.getRecord().offset(),
                message.getRecord().value());
        }
    }

    @Override
    public void failure(TopicContext topicContext, Message message, Throwable cause) {
        log.error("Message implicitly failed [topic-partition: {}, offset: {}, eventPacket: {}]",
            message.getTopicPartition(), message.getRecord().offset(),
            message.getRecord().value());
        log.error("Message implicitly failed", cause);
    }

    @Override
    public boolean stop(TopicContext topicContext) throws InterruptedException {
        log.debug("Stopped RetryFailureStrategy [topic: {}]", topicContext.getTopic());
        boolean result = true;
        log.debug("Stopped RetryFailureStrategy [topic: {}, result: {}]", topicContext.getTopic(), result);
        return result;
    }
}
