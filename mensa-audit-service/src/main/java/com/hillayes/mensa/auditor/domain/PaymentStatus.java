package com.hillayes.mensa.auditor.domain;

public enum PaymentStatus {
    BANK_PAYMENT_REQUESTED(false),
    ACCEPTED_BY_RAILS(false),
    CONFIRMED(false),
    RETURNED(false),
    SUBMITTED(false),
    CANCELLED(false),
    ACCEPTED(true),
    REJECTED(false),
    AWAITING_FUNDS(false),
    FUNDED(false),
    UNFUNDED(true),
    WITHDRAWN(false),
    FAILED(false);

    private final boolean withdrawable;

    PaymentStatus(boolean withdrawable) {
        this.withdrawable = withdrawable;
    }

    public boolean isWithdrawable() {
        return withdrawable;
    }
}
