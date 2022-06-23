package com.hillayes.mensa.payment.service;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.payment.domain.Payout;
import com.hillayes.mensa.payment.domain.PayoutStatus;
import com.hillayes.mensa.payment.events.PayoutEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PayoutService {
    private final PayoutEventSender payoutEventSender;

    public Payout createPayout(Payout payout) {
        log.info("Creating payout [payorId: {}, memo: {}]", payout.getPayoutToPayorId(), payout.getPayoutMemo());

        payout.setPayoutId(randomUUID());
        payoutEventSender.sendPayout(payout);

        log.debug("Created payout [payoutId: {}, payorId: {}, memo: {}]",
            payout.getPayoutId(), payout.getPayoutToPayorId(), payout.getPayoutMemo());
        return payout;
    }

    public Payout updatePayout(UUID id, Payout payout) {
        log.info("Updating payout [payoutId: {}, payorId: {}, memo: {}]",
            id, payout.getPayoutToPayorId(), payout.getPayoutMemo());

        payout.setPayoutId(id);
        payoutEventSender.sendPayout(payout);

        log.debug("Updated payout [payoutId: {}, payorId: {}, memo: {}]",
            payout.getPayoutId(), payout.getPayoutToPayorId(), payout.getPayoutMemo());
        return payout;
    }
}
