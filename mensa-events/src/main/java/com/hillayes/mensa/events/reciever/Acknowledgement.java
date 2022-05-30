package com.hillayes.mensa.events.reciever;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.atomic.AtomicLong;

public class Acknowledgement {
    private final Consumer<?,?> consumer;
    private final AtomicLong offset = new AtomicLong();

    public Acknowledgement(Consumer<?,?> aConsumer) {
        consumer = aConsumer;
    }

    public void ack(ConsumerRecord<?,?> aRecord) {
        offset.accumulateAndGet(aRecord.offset(), Math::max);
    }
}
