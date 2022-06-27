package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankPaymentRequested {

    @NotNull(message = "physicalAcctName is mandatory")
    private String physicalAcctName;

    @NotNull(message = "Rails Id is mandatory")
    private String railsId;

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    @NotNull(message = "Payee Id is mandatory")
    private UUID payeeId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    @NotNull(message = "Remote Id is mandatory")
    private String remoteId;

    @NotNull(message = "Amount is mandatory")
    @Min(value = 1, message = "Amount must be greater than zero and less than 9,999,999,999")
    @Max(value = 9_999_999_999L, message = "Amount must be greater than zero and less than 9,999,999,999")
    private Long amount;    

    @NotNull(message = "Currency is mandatory")
    @Valid
    private CurrencyType currency;

    @NotNull(message = "Payor reference is mandatory")
    private String payorReference;
}
