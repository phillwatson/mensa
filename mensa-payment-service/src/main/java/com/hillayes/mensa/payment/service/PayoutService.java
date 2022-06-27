package com.hillayes.mensa.payment.service;

import com.hillayes.mensa.payment.events.PayoutEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PayoutService {
    private final PayoutEventSender payoutEventSender;
}
