package com.hillayes.mensa.events.events.payout;

import java.util.UUID;

/**
 * Supertype of events which represent the result of processing a PaymentSubmitted
 */
public interface PaymentSubmittedResult {
    UUID getPaymentId();
    PayoutPayorIds getPayoutPayorIds();
    UUID getPayoutId();
}
