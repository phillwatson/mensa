package com.hillayes.mensa.outbox.config;

import com.hillayes.mensa.outbox.sender.EventPacketSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
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
}