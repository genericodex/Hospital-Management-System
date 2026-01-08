package com.pahappa.models.dto;

import com.pahappa.models.Patient;

import java.io.Serializable;

/**
 * A Data Transfer Object (DTO) to hold summarized billing information for a patient.
 * This is used to display an aggregated view on the main billings page.
 */
public class PatientBillingSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private Patient patient;
    private double totalPendingAmount;
    private long pendingBillsCount;

    public PatientBillingSummary(Patient patient, Double totalPendingAmount, Long pendingBillsCount) {
        this.patient = patient;
        // Handle null from SUM() if no pending bills exist
        this.totalPendingAmount = (totalPendingAmount != null) ? totalPendingAmount : 0.0;
        this.pendingBillsCount = (pendingBillsCount != null) ? pendingBillsCount : 0L;
    }

    // Getters and Setters
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public double getTotalPendingAmount() {
        return totalPendingAmount;
    }

    public void setTotalPendingAmount(double totalPendingAmount) {
        this.totalPendingAmount = totalPendingAmount;
    }

    public long getPendingBillsCount() {
        return pendingBillsCount;
    }

    public void setPendingBillsCount(long pendingBillsCount) {
        this.pendingBillsCount = pendingBillsCount;
    }
}
