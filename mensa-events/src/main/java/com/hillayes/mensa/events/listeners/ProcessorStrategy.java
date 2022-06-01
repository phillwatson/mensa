package com.hillayes.mensa.events.listeners;

import com.hillayes.mensa.events.domain.Message;

public interface ProcessorStrategy {
    void process(TopicContext context, Message message);

    boolean stop(TopicContext topicContext) throws InterruptedException;
}
