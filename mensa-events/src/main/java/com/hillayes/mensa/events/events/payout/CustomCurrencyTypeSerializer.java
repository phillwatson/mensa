package com.hillayes.mensa.events.events.payout;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class CustomCurrencyTypeSerializer extends StdSerializer<CurrencyType> {
    protected CustomCurrencyTypeSerializer() {
        super(CurrencyType.class);
    }

    public void serialize(CurrencyType currencyType, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(currencyType.getCurrencyCode());
    }

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        if (visitor != null) {
            visitor.expectStringFormat(typeHint);
        }
    }
}
