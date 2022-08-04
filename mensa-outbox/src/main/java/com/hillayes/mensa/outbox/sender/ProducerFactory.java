package com.hillayes.mensa.outbox.sender;

import com.hillayes.mensa.outbox.config.ProducerBean;
import com.hillayes.mensa.outbox.domain.EventPacket;
import io.quarkus.runtime.ShutdownEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
@Slf4j
public class ProducerFactory {
    private final Properties producerConfig;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final List<Producer<String, EventPacket>> instances = new ArrayList<>();

    private final ThreadLocal<Producer<String, EventPacket>> producers = new ThreadLocal<>() {
        protected Producer<String, EventPacket> initialValue() {
            log.debug("Creating new Producer");
            synchronized (instances) {
                Producer<String, EventPacket> result = new KafkaProducer<>(producerConfig);
                instances.add(result);
                return result;
            }
        }
    };

    public ProducerFactory(@ProducerBean Properties producerConfig) {
        this.producerConfig = producerConfig;
    }

    public Producer<String, EventPacket> getProducer() {
        if (shutdown.get()) {
            throw new RuntimeException("Application shutting down. Unable to issue Message Producer");
        }
        return producers.get();
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("Shutting down Message Producers - started");
        shutdown.set(true);
        instances.forEach(p -> {
            log.info("Closing producer {}", p);
            p.close();
        });
        log.info("Shutting down Message Producers - complete");
    }
}
