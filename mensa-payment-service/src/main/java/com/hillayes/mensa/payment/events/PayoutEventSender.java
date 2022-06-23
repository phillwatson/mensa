package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.events.domain.Topic;
import com.hillayes.mensa.events.events.payment.PayoutEvent;
import com.hillayes.mensa.events.sender.EventSender;
import com.hillayes.mensa.payment.domain.Payout;
import com.hillayes.mensa.payment.domain.PayoutStatus;
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

    public Future<RecordMetadata> sendPayout(Payout payout) {
        Topic topic = getTopic(payout.getStatus());

        log.debug("Sending {} event [payoutId: {}, payorId: {}, memo: {}]",
            topic, payout.getPayoutId(), payout.getPayoutToPayorId(), payout.getPayoutMemo());
        return eventSender.send(topic, marshal(payout));
    }

    private PayoutEvent marshal(Payout payout) {
        return PayoutEvent.builder()
            .payoutId(payout.getPayoutId())
            .submittingPayorId(payout.getSubmittingPayorId())
            .payoutFromPayorId(payout.getPayoutFromPayorId())
            .payoutToPayorId(payout.getPayoutToPayorId())
            .payoutMemo(payout.getPayoutMemo())
            .status((payout.getStatus() == null) ? null : payout.getStatus().name())
            .submittedDateTime(payout.getSubmittedDateTime())
            .quotedDateTime(payout.getQuotedDateTime())
            .instructedDateTime(payout.getInstructedDateTime())
            .withdrawnDateTime(payout.getWithdrawnDateTime())
            .build();
    }

    private Topic getTopic(PayoutStatus status) {
        if (status != null) {
            switch (status) {
                case INSTRUCTED:
                    return Topic.PAYOUT_INSTRUCTED;
                case CONFIRMED:
                    return Topic.PAYOUT_CONFIRMED;
                case INCOMPLETE:
                    return Topic.PAYOUT_INCOMPLETE;
                case COMPLETED:
                    return Topic.PAYOUT_COMPLETED;
                case SUBMITTED:
                    return Topic.PAYOUT_SUBMITTED;
                case ACCEPTED:
                    return Topic.PAYOUT_ACCEPTED;
                case REJECTED:
                    return Topic.PAYOUT_REJECTED;
                case QUOTED:
                    return Topic.PAYOUT_QUOTED;
                case WITHDRAWN:
                    return Topic.PAYOUT_WITHDRAWN;
            }
        }
        return Topic.PAYOUT_INSTRUCTED;
    }
}
