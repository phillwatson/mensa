package com.hillayes.mensa.auditor.repository.delta;

import com.hillayes.mensa.events.events.payout.CurrencyType;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public abstract class AbstractDeltaStrategy implements DeltaStrategy {
    // a common update filter used by most strategies
    protected static final UpdateOptions UPSERT_OPTION = new UpdateOptions().upsert(true);

    private String forEventClass;

    protected AbstractDeltaStrategy(Class<?> forEventClass) {
        this.forEventClass = forEventClass.getName();
    }

    @Override
    public boolean isApplicable(String payloadClass) {
        return forEventClass.equalsIgnoreCase(payloadClass);
    }

    protected static <T> BsonDocument encode(CodecRegistry codecRegistry, T instance) {
        BsonDocument result = new BsonDocument();
        encode(new BsonDocumentWriter(result), codecRegistry, instance);
        return result;
    }

    protected static <T> void encode(BsonDocumentWriter writer, CodecRegistry codecRegistry, T instance) {
        Codec<T> codec = (Codec<T>) codecRegistry.get(instance.getClass());
        codec.encode(writer, instance, EncoderContext.builder().build());
    }

    protected static String currencyCode(CurrencyType currencyType) {
        return currencyType == null ? null : currencyType.getCurrencyCode();
    }
}
