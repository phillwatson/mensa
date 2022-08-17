package com.hillayes.mensa.events.events.payout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailed {

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @NotNull(message = "Reason is mandatory")
    private String reason;

    // optional amount - should be source/settlement
    @Min(value = 1)
    @Max(value = 9_999_999_999L)
    private Long amount;

    // provided if amount is specified
    @Valid
    private CurrencyType currency;

    @JsonIgnore
    @AssertTrue(message = "Currency must be provided with Amount and not otherwise")
    public boolean isAmountProvidedWithCurrency() {
        if ( amount == null ){
            return currency == null;
        }
        else{
            return currency != null;
        }
    }
}
