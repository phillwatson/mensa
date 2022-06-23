package com.hillayes.mensa.events.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum Topic {
    USER_CREATED("User profile has been created, but not on-boarded."),
    USER_ONBOARDED("User has accepted invitation and has been on-boarded."),
    USER_DECLINED("User has declined invitation."),
    USER_UPDATED("User profile has been updated."),
    USER_DELETED("User profile has been deleted."),

    PAYOUT_INSTRUCTED("Payout has been submitted by the payor."),
    PAYOUT_CONFIRMED("Payout has been been confirmed."),
    PAYOUT_INCOMPLETE("Payout is incomplete."),
    PAYOUT_COMPLETED("Payout has been completed."),
    PAYOUT_SUBMITTED("Payout has been submitted."),
    PAYOUT_ACCEPTED("Payout has been accepted, and awaits approval or rejection."),
    PAYOUT_REJECTED("Payout has been rejected."),
    PAYOUT_QUOTED("Payout has been quoted by the exchange."),
    PAYOUT_WITHDRAWN("Payout has been been withdrawn/cancelled."),

    PAYMENT_BANK_PAYMENT_REQUESTED("Payment has been requested by the payee."),
    PAYMENT_ACCEPTED_BY_RAILS("Payment has been accepted, and awaits approval or rejection."),
    PAYMENT_CONFIRMED("Payment has been confirmed."),
    PAYMENT_RETURNED("Payment has been returned after rejection."),
    PAYMENT_SUBMITTED("Payment has been submitted."),
    PAYMENT_CANCELLED("Payment has been cancelled."),
    PAYMENT_ACCEPTED("Payment has been accepted, and awaits approval or rejection."),
    PAYMENT_REJECTED("Payment has been rejected."),
    PAYMENT_AWAITING_FUNDS("Payment is awaiting funds."),
    PAYMENT_FUNDED("Payment has been funded."),
    PAYMENT_UNFUNDED("Payment funds are not available."),
    PAYMENT_WITHDRAWN("Payment has been withdrawn by rails service."),
    PAYMENT_FAILED("Payment has marked as failed by rails service.");

    private String summary;

    private Topic(String summary) {
        this.summary = summary;
    }

    public String topicName() {
        return name().toLowerCase();
    }

    public String getSummary() {
        return summary;
    }

    public static Collection<String> allTopicNames() {
        return Arrays.stream(Topic.values()).map(Topic::topicName).collect(Collectors.toList());
    }
}
