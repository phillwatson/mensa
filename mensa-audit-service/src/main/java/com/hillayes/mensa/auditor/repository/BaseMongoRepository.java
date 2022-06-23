package com.hillayes.mensa.auditor.repository;

import com.mongodb.MongoClientSettings;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.UuidRepresentation;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.UUID;

public abstract class BaseMongoRepository<T> implements PanacheMongoRepositoryBase<T, UUID> {

    protected BaseMongoRepository() {
        CodecRegistries.fromRegistries(
            // Put a UuidCodec with representation specified in the first provider, which
            // will override the default one with no representation specified
            CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
            MongoClientSettings.getDefaultCodecRegistry());
    }

    protected <T> BsonDocument encode(T instance) {
        CodecRegistry codecRegistry = mongoCollection().getCodecRegistry();
        Codec<T> codec = (Codec<T>) codecRegistry.get(instance.getClass());

        BsonDocument result = new BsonDocument();
        codec.encode(new BsonDocumentWriter(result), instance, EncoderContext.builder().build());

        return result;
    }
}
