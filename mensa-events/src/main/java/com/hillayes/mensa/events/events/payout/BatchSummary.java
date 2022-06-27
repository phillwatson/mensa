package com.hillayes.mensa.events.events.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSummary {

	@NotNull
	private UUID batchId;

	@NotNull
	@Min(0)
	@Max(2000)
	Integer numberOfAcceptedPayments;

	@NotNull
	@Min(0)
	@Max(2000)
	Integer numberOfRejectedPayments;

	// optional as rejected batches do not have source currency populated
	CurrencyType sourceCurrency;
}
