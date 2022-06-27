package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * One PaymentSubmitted will be emitted for every valid payment in a payout
 * This event will be consumed by a rails service with matching railsId
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubmitted {

    private String railsId;

    @NotNull(message = "Payout Id is mandatory")
    private UUID payoutId;

    // The batch id from the payout in payout-service
    private UUID batchId;

    @NotNull(message = "Payment Id is mandatory")
    private UUID paymentId;

    private UUID payeeId;

    @Min(value = 1, message = "Total Payments in Batch must be at least 1")
    private int totalPaymentsInBatch;

    private UUID sourceAccountId;

    @Valid
    @NotNull(message = "payoutPayorIds is mandatory")
    private PayoutPayorIds payoutPayorIds;

    private String remoteId;
    private String remoteSystemId;

    // 1. payment fields
    private String payoutMemo;
    private String paymentMemo;
    private String paymentReference;

    // used to embed things like Invoice Data (Mastercard Track)
    private String paymentMetadata;

    // payment amount in minor units
    @NotNull(message = "Amount is mandatory")
    @Min(value = 1, message = "Amount must be at least 1")
    private Long amount;
    // the currency that "amount" refers to
    @NotNull(message = "Currency is mandatory")
    private CurrencyType currency;
    // the currency of the source account e.g. we're paying a payee 10000 GBP ( £100 ) and settling in USD
    @NotNull(message = "Settlement Currency is mandatory")
    private CurrencyType settlementCurrency;

    // 2. payee details
    private PayeeType payeeType;
    // if payeeType is not null then either firstName+lastName or businessName must be specified
    private String payeeFirstName;
    private String payeeLastName;
    private String payeeBusinessName;

    private String payeeAddressLine1;
    private String payeeAddressCity;
    private String payeeAddressStateOrProv;
    private String payeeAddressZipOrPostal;
    private String payeeAddressCountryCode;

    // 3. payment channel info
    private String routingNumber;
    @Size(min = 4, max = 34, message = "Account number must be between {min} and {max} (inclusive) characters")
    private String accountNumber;
    @Size(min=15, max=34, message="Iban must be between {min} and {max} characters long")
    private String iban;

    @Size(max = 40, message = "Payor payment Id must not be longer than {max} characters long")
    private String payorPaymentId;

    @Size(min = 1, max = 100, message = "Account name has a max length of {max} characters")
    private String accountName;

    private UUID paymentChannelId;

    private String paymentChannelName;

    private String payeeBankAddressCountryCode;
    private String payeeBankName;       // from bank lookup service
    private String payeeBankSwiftBic;   // from bank lookup service

    private TransmissionType transmissionType;

    @NotNull(message = "Payor Payment Permissions is mandatory")
    private List<TransmissionType> permittedTransmissionTypes;

    private String sourceAccountCountry;

    @AssertTrue(message="Either payee first name and last name for Individual payee, or only business name must be set for Company payee")
    private boolean isPayeeNameInfoValid() {
        if (payeeType == null) {
            // Do nothing here - validation will fail on payeeType NotNull annotation
            return true;
        }

        if (payeeType == PayeeType.Individual && payeeFirstName != null && payeeLastName != null && payeeBusinessName == null) {
            return true;
        } else if (payeeType == PayeeType.Company && payeeFirstName == null && payeeLastName == null && payeeBusinessName != null) {
            return true;
        }
        return false;
    }

}
