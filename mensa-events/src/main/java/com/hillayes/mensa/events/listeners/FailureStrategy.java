package com.hillayes.mensa.events.listeners;

import com.hillayes.mensa.events.domain.Message;

public interface FailureStrategy {
    void failure(TopicContext topicContext, Message message);
    void failure(TopicContext topicContext, Message message, Throwable cause);

    boolean stop(TopicContext topicContext) throws InterruptedException;
}
