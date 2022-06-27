package com.hillayes.mensa.payment.events;

import com.hillayes.mensa.events.sender.EventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PayoutEventSender {
    private final EventSender eventSender;
}
