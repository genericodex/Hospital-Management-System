package com.pahappa.beans;

import com.pahappa.constants.BillingStatus;
import com.pahappa.constants.PaymentMethod;
import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import com.pahappa.services.BillingServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
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
    private BillingServiceImpl billingService;

    private List<Billing> billings;
    private List<Billing> selectedBillings;
    private Billing selectedBilling;
    private List<Patient> patients;
    private String paymentMethod;
    private boolean newBilling;
    private Integer searchPatientId;
    private List<Billing> deletedBillings;
    private Long billingToDeleteId;

    @PostConstruct
    public void init() {
        if (billingService == null) {
            billings = new java.util.ArrayList<>();
            patients = new java.util.ArrayList<>();
            selectedBilling = new Billing();
            return;
        }
        try {
            billings = billingService.getAllBillings();
        } catch (Exception e) {
            billings = new java.util.ArrayList<>();
        }
        if (billings == null) {
            billings = new java.util.ArrayList<>();
        }
        try {
            patients = billingService.getAllPatients();
        } catch (Exception e) {
            patients = new java.util.ArrayList<>();
        }
        if (patients == null) {
            patients = new java.util.ArrayList<>();
        }
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
                billingService.createBilling(
                        selectedBilling.getPatient(),
                        selectedBilling.getAmount(),
                        selectedBilling.getServiceDescription()
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Bill created successfully"));
            } else {
                // For updates, get fresh entity and copy changes
                Billing managedBilling = billingService.getBillingById(selectedBilling.getId());
                managedBilling.setAmount(selectedBilling.getAmount());
                managedBilling.setServiceDescription(selectedBilling.getServiceDescription());
                managedBilling.setBillDate(selectedBilling.getBillDate());
                managedBilling.setStatus(selectedBilling.getStatus());
                managedBilling.setPaymentMethod(selectedBilling.getPaymentMethod());
                billingService.processPayment(managedBilling, managedBilling.getPaymentMethod());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Bill updated successfully"));
            }
            billings = billingService.getAllBillings();
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
            Billing managedBilling = billingService.getBillingById(selectedBilling.getId());
            if (managedBilling != null) {
                billingService.processPayment(managedBilling, paymentMethod);
                billings = billingService.getAllBillings();
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

    public void softDeleteBilling(Billing billing) {
        try {
            Billing managedBilling = billingService.getBillingById(billing.getId());
            if (managedBilling != null) {
                managedBilling.setDeleted(true);
                billingService.updateBilling(managedBilling);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Bill deleted successfully"));
                billings = billingService.getAllBillings();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Billing record not found for ID: " + billing.getId()));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void searchBillsByPatient() {
        if (searchPatientId == null) {
            billings = billingService.getAllBillings();
            return;
        }
        billings = billingService.getBillingsByPatient(searchPatientId.longValue());
    }

    public void clearPatientSearch() {
        searchPatientId = null;
        billings = billingService.getAllBillings();
    }

    public void loadDeletedBillings() {
        deletedBillings = billingService.getDeletedBillings();
    }

    public void restoreBilling(Long id) {
        try {
            Billing billing = billingService.getBillingById(id);
            if (billing != null && billing.isDeleted()) {
                billing.setDeleted(false);
                billingService.updateBilling(billing);
                loadDeletedBillings();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Billing restored successfully"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void hardDeleteBilling(Long id) {
        try {
            billingService.deleteBilling(id);
            loadDeletedBillings();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Billing permanently deleted"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
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
    public Integer getSearchPatientId() { return searchPatientId; }
    public void setSearchPatientId(Integer searchPatientId) { this.searchPatientId = searchPatientId; }
    public void setBillingToDeleteId(Long id) {
        this.billingToDeleteId = id;
    }
    public Long getBillingToDeleteId() {
        return billingToDeleteId;
    }
    public List<Billing> getDeletedBillings() {
        if (deletedBillings == null) {
            loadDeletedBillings();
        }
        return deletedBillings;
    }
}

