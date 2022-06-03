package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.Message;

public interface ProcessorStrategy {
    void process(TopicListener topicListener, Message message);

    boolean stop(TopicListener topicListener);
}
