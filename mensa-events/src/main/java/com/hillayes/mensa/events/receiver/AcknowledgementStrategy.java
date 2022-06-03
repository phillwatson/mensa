package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.Message;

public interface AcknowledgementStrategy {
    void received(TopicListener topicListener, Message message);
    void ack(TopicListener topicListener, Message message);
    boolean stop(TopicListener topicListener);
}
