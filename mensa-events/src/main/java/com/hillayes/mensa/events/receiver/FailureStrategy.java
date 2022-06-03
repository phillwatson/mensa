package com.hillayes.mensa.events.receiver;

import com.hillayes.mensa.events.domain.Message;

public interface FailureStrategy {
    void failure(TopicListener topicListener, Message message);
    void failure(TopicListener topicListener, Message message, Throwable cause);
    boolean stop(TopicListener topicListener);
}
