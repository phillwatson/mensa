package com.hillayes.mensa.events.events.payout;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonSerialize(using = CustomCurrencyTypeSerializer.class)
@JsonDeserialize(using = CustomCurrencyTypeDeserializer.class)
@Data
@RequiredArgsConstructor
public class CurrencyType {
    @NotNull @Size(min = 3,max = 3)
    private final String currencyCode;

    public static CurrencyType valueOf(String currencyCode) {
        if (currencyCode == null) {
            throw new NullPointerException("currencyCode cannot be null");
        } else {
            return new CurrencyType(currencyCode);
        }
    }
}
