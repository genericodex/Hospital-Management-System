package com.pahappa.services.billing;

import com.pahappa.models.Billing;
import com.pahappa.models.Patient;

import java.util.List;

public interface BillingService {

    Billing createBilling(Patient patient, double amount, String description);
    List<Billing> getAllBillings();
    Billing getBillingById(Long id);
    List<Billing> getBillingsByPatient(Long patientId);
    void updateBilling(Billing billing);
    void processPayment(Billing billing, String paymentMethod);
    void deleteBilling(Long id);
    void softDeleteBilling(Long id);
    void restoreBilling(Long id);

}
