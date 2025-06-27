package com.pahappa.beans;

import com.pahappa.constants.BillingStatus;
import com.pahappa.constants.PaymentMethod;
import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import com.pahappa.services.HospitalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class BillingBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private HospitalService hospitalService;

    private List<Billing> billings;
    private List<Billing> selectedBillings;
    private Billing selectedBilling;
    private List<Patient> patients;
    private String paymentMethod;
    private boolean newBilling;

    @PostConstruct
    public void init() {
        billings = hospitalService.getAllBillings();
        patients = hospitalService.getAllPatients();
        selectedBilling = new Billing();
    }

    public void initNewBilling() {
        selectedBilling = new Billing();
        selectedBilling.setBillDate(new Date());
        newBilling = true;
    }

    public void saveBilling() {
        try {
            if (selectedBilling.getId() == null) { // New bill
                hospitalService.createBilling(
                        selectedBilling.getPatient(),
                        selectedBilling.getAmount(),
                        selectedBilling.getServiceDescription()
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Bill created successfully"));
            } else {
                // For updates, get fresh entity and copy changes
                Billing managedBilling = hospitalService.getBillingById(selectedBilling.getId());
                managedBilling.setAmount(selectedBilling.getAmount());
                managedBilling.setServiceDescription(selectedBilling.getServiceDescription());
                managedBilling.setBillDate(selectedBilling.getBillDate());
                managedBilling.setStatus(selectedBilling.getStatus());
                managedBilling.setPaymentMethod(selectedBilling.getPaymentMethod());
                hospitalService.processPayment(managedBilling, managedBilling.getPaymentMethod());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Bill updated successfully"));
            }
            billings = hospitalService.getAllBillings();
            // Reset selectedBilling and newBilling after save/update
            selectedBilling = new Billing();
            newBilling = false;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void initPayment(Billing billing) {
        selectedBilling = billing;
        paymentMethod = null;
    }

    public void processPayment() {
        try {
            if (selectedBilling == null || selectedBilling.getId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No bill selected or bill ID is missing."));
                return;
            }
            Billing managedBilling = hospitalService.getBillingById(selectedBilling.getId());
            if (managedBilling != null) {
                hospitalService.processPayment(managedBilling, paymentMethod);
                billings = hospitalService.getAllBillings();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Payment processed successfully"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Billing record not found for ID: " + selectedBilling.getId()));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void viewDetails(Billing billing) {
        selectedBilling = billing;
    }

    public BillingStatus[] getStatuses() {
        return BillingStatus.values();
    }

    public PaymentMethod[] getPaymentMethods() {
        return PaymentMethod.values();
    }

    // Getters and Setters
    public List<Billing> getBillings() { return billings; }
    public List<Billing> getSelectedBillings() { return selectedBillings; }
    public void setSelectedBillings(List<Billing> selectedBillings) { this.selectedBillings = selectedBillings; }
    public Billing getSelectedBilling() { return selectedBilling; }
    public void setSelectedBilling(Billing selectedBilling) { this.selectedBilling = selectedBilling; }
    public List<Patient> getPatients() { return patients; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isNewBilling() { return newBilling; }
}