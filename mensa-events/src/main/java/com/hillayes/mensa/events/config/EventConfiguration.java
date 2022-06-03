package com.hillayes.mensa.events.config;

import com.hillayes.mensa.events.receiver.EventPacketDeserializer;
import com.hillayes.mensa.events.sender.EventPacketSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Produces;
import java.util.Properties;

@Slf4j
public class EventConfiguration {
    @Produces()
    @ProducerBean
    public Properties getProducerConfig(@ConfigProperty(name="kafka.producer.client", defaultValue = "mensa") final String clientId,
                                        @ConfigProperty(name="kafka.bootstrap.servers", defaultValue = "kafka:9092") final String bootstrapServers,
                                        @ConfigProperty(name="kafka.producer.lingerMs", defaultValue = "0") final String lingerMs,
                                        @ConfigProperty(name="kafka.producer.acksConfig", defaultValue = "all") final String acksConfig,
                                        @ConfigProperty(name="kafka.producer.maxInFlightRequestsPerConnection", defaultValue = "3") final Integer maxInFlightRequestsPerConnection,
                                        @ConfigProperty(name="kafka.producer.batchSizeConfig", defaultValue = "16384") final Integer batchSizeConfig,
                                        @ConfigProperty(name="kafka.producer.maxBlockMsConfig", defaultValue = "60000") final Integer maxBlockMsConfig) {
        Properties config = new Properties();
        config.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.ACKS_CONFIG, acksConfig);
        config.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventPacketSerializer.class);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSizeConfig);
        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMsConfig);

        log.debug("Kafka producer config: {}", config);
        return config;
    }

    @Produces()
    @ConsumerBean
    public Properties getConsumerConfig(@ConfigProperty(name="kafka.consumer.client", defaultValue = "mensa") final String clientId,
                                        @ConfigProperty(name="kafka.bootstrap.servers", defaultValue = "kafka:9092") final String bootstrapServers,
                                        @ConfigProperty(name="mensa.events.group") final String groupId) {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventPacketDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        log.debug("Kafka consumer config: {}", config);
        return config;
    }
}
