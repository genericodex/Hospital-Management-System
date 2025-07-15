package com.pahappa.services.billing;

import com.pahappa.models.Billing;
import com.pahappa.models.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    List<Billing> getDeletedBillings();
    List<Billing> findBillings(Long patientId, String paymentMethod);
    Map<LocalDate, Double> getDailyRevenue(LocalDate startDate, LocalDate endDate);
    double getTotalRevenue();
}
