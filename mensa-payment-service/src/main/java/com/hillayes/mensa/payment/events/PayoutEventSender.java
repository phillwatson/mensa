package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.payment.PayoutEvent;
import com.hillayes.mensa.events.sender.EventSender;
import com.hillayes.mensa.payment.domain.Payout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Future;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PayoutEventSender {
    private final EventSender eventSender;

    public Future<RecordMetadata> sendPayoutCreated(Payout payout) {
        log.debug("Sending PayoutCreated event [payoutId: {}, payorId: {}, memo: {}]",
            payout.getPayoutId(), payout.getPayoutToPayorId(), payout.getPayoutMemo());
        return eventSender.send(Topic.PAYOUT_CREATED, PayoutEvent.builder()
            .payoutId(payout.getPayoutId())
            .submittingPayorId(payout.getSubmittingPayorId())
            .payoutFromPayorId(payout.getPayoutFromPayorId())
            .payoutToPayorId(payout.getPayoutToPayorId())
            .payoutMemo(payout.getPayoutMemo())
            .build());
    }

    public Future<RecordMetadata> sendPayoutAccepted(Payout payout) {
        log.debug("Sending PayoutAccepted event [payoutId: {}, payorId: {}, memo: {}]",
            payout.getPayoutId(), payout.getPayoutToPayorId(), payout.getPayoutMemo());
        return eventSender.send(Topic.PAYOUT_ACCEPTED, PayoutEvent.builder()
            .payoutId(payout.getPayoutId())
            .submittingPayorId(payout.getSubmittingPayorId())
            .payoutFromPayorId(payout.getPayoutFromPayorId())
            .payoutToPayorId(payout.getPayoutToPayorId())
            .payoutMemo(payout.getPayoutMemo())
            .build());
    }
}
