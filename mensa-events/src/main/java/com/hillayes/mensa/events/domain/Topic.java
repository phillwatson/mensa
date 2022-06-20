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

    PAYOUT_CREATED("Payout has been created, and awaits acceptance into system."),
    PAYOUT_ACCEPTED("Payout has been accepted, and awaits approval or rejection."),
    PAYOUT_APPROVED("Payout has been approved."),
    PAYOUT_REJECTED("Payout has been rejected."),
    PAYOUT_COMPLETE("Payout has been completed after approval."),
    PAYOUT_RETURNED("Payout has been returned after rejection."),

    PAYMENT_CREATED("Payment has been created, and awaits acceptance into system."),
    PAYMENT_ACCEPTED("Payment has been accepted, and awaits approval or rejection."),
    PAYMENT_APPROVED("Payment has been approved."),
    PAYMENT_REJECTED("Payment has been rejected."),
    PAYMENT_COMPLETE("Payment has been completed after approval."),
    PAYMENT_RETURNED("Payment has been returned after rejection.");

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
