package com.hillayes.mensa.events.domain;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

public abstract class Message {
    private final TopicPartition topicPartition;
    private final ConsumerRecord<String, EventPacket> record;

    public Message(TopicPartition topicPartition, ConsumerRecord<String, EventPacket> record) {
        this.topicPartition = topicPartition;
        this.record = record;
    }

    public TopicPartition getTopicPartition() {
        return topicPartition;
    }

    public ConsumerRecord<String, EventPacket> getRecord() {
        return record;
    }

    public abstract void ack();

    public abstract void nack();

    public abstract void nack(Throwable cause);
}
