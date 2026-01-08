package com.pahappa.beans;

import com.pahappa.constants.PaymentMethod;
import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import com.pahappa.services.billing.BillingService;
import com.pahappa.services.patient.PatientService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class PatientBillingDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private BillingService billingService;

    @Inject
    private PatientService patientService;

    private Long patientId;
    private Patient patient;
    private List<Billing> billsForPatient;
    private double totalPendingAmount;
    private PaymentMethod selectedPaymentMethod;

    @PostConstruct
    public void init() {
        // This method is intentionally left empty.
        // The loading logic is triggered by f:viewAction in the XHTML.
    }

    // This method is called by the view to load data based on the patientId from the URL
    public void loadPatientBillingData() {
        if (patientId == null) {
            // Handle case where no ID is provided (e.g., direct navigation)
            return;
        }
        this.patient = patientService.getPatientById(patientId);
        this.billsForPatient = billingService.getBillingsByPatient(patientId);
        calculateTotalPending();
    }

    private void calculateTotalPending() {
        if (billsForPatient == null) {
            this.totalPendingAmount = 0.0;
            return;
        }
        this.totalPendingAmount = billsForPatient.stream()
                .filter(b -> b.getStatus() == com.pahappa.constants.BillingStatus.PENDING && !b.isDeleted())
                .mapToDouble(Billing::getAmount)
                .sum();
    }

    public void processSinglePayment(Billing billToPay) {
        if (selectedPaymentMethod == null) {
            addMessage(FacesMessage.SEVERITY_WARN, "Payment Method Required", "Please select a payment method.");
            return;
        }
        try {
            billingService.processPayment(billToPay, selectedPaymentMethod.name());
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Payment for Bill #" + billToPay.getId() + " processed.");
            // Refresh data on the page
            loadPatientBillingData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not process payment.");
        }
    }

    public void processAllPendingPayments() {
        if (selectedPaymentMethod == null) {
            addMessage(FacesMessage.SEVERITY_WARN, "Payment Method Required", "Please select a payment method to pay all.");
            return;
        }
        try {
            List<Billing> pendingBills = billsForPatient.stream()
                    .filter(b -> b.getStatus() == com.pahappa.constants.BillingStatus.PENDING && !b.isDeleted())
                    .collect(Collectors.toList());

            if (pendingBills.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_INFO, "No Pending Bills", "There are no pending bills to pay.");
                return;
            }

            for (Billing bill : pendingBills) {
                billingService.processPayment(bill, selectedPaymentMethod.name());
            }

            addMessage(FacesMessage.SEVERITY_INFO, "Success", "All pending bills have been paid.");
            // Refresh data on the page
            loadPatientBillingData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while processing payments.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters for enums
    public PaymentMethod[] getPaymentMethods() {
        return PaymentMethod.values();
    }

    // NEW: Add this helper method here
    public String getPaymentMethodDisplayName(String paymentMethodName) {
        // If there's no payment method name, return "Not Applicable" or an empty string.
        if (paymentMethodName == null || paymentMethodName.trim().isEmpty()) {
            return "N/A";
        }
        try {
            // Find the enum constant corresponding to the string (e.g., "MOBILE_MONEY").
            PaymentMethod method = PaymentMethod.valueOf(paymentMethodName);
            // Return its user-friendly display name ("Mobile Money").
            return method.getDisplayName();
        } catch (IllegalArgumentException e) {
            // This is a safety net. If the string from the database doesn't match any
            // known enum, just show the raw value so you don't get an error.
            return paymentMethodName;
        }
    }


    // Standard Getters and Setters
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Patient getPatient() { return patient; }
    public List<Billing> getBillsForPatient() { return billsForPatient; }
    public double getTotalPendingAmount() { return totalPendingAmount; }
    public PaymentMethod getSelectedPaymentMethod() { return selectedPaymentMethod; }
    public void setSelectedPaymentMethod(PaymentMethod selectedPaymentMethod) { this.selectedPaymentMethod = selectedPaymentMethod; }
}
