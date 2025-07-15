package com.pahappa.constants;


public enum AppointmentStatus {
    SCHEDULED("SCHEDULED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    NO_SHOW("NO SHOW");


    // Field to store the display name
    private final String displayName;

    // Constructor to initialize the display name
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    // Public getter method that the XHTML page will call
    public String getDisplayName() {
        return displayName;
    }
}
