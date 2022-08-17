package com.hillayes.mensa.events.events.payout;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class CustomCurrencyTypeDeserializer extends JsonDeserializer<CurrencyType> {
    public CustomCurrencyTypeDeserializer() {
    }

    public CurrencyType deserialize(JsonParser jsonparser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String jsonSnippet = jsonparser.getText();
        return CurrencyType.valueOf(jsonSnippet);
    }
}
