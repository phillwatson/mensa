package com.hillayes.mensa.events.listeners;

import com.hillayes.mensa.events.domain.Message;

public interface AcknowledgementStrategy {
    void received(TopicContext topicContext, Message message);

    void ack(TopicContext topicContext, Message message);

    boolean stop(TopicContext topicContext) throws InterruptedException;
}
