package com.pahappa.constants;


public enum PaymentMethod {
    CASH("CASH"),
    CREDIT_CARD("CREDIT CARD"),
    DEBIT_CARD("DEBIT CARD"),
    INSURANCE("INSURANCE"),
    MOBILE_MONEY("MOBILE MONEY");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

