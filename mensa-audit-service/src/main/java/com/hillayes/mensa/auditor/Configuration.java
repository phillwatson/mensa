package com.hillayes.mensa.auditor;

import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecProvider;

import javax.enterprise.inject.Produces;

@Slf4j
public class Configuration {
    @Produces
    public CodecProvider uuidCodecProvider() {
        log.debug("Registering UUID Codec Provider");
        return new UuidCodecProvider(UuidRepresentation.STANDARD);
    }
}
