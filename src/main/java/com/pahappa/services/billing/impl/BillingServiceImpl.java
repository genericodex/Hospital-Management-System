package com.pahappa.services.billing.impl;

import com.pahappa.constants.BillingStatus;
import com.pahappa.dao.BillingDao;
import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import java.util.List;

import com.pahappa.services.HospitalService;
import com.pahappa.services.billing.BillingService;
import jakarta.enterprise.context.ApplicationScoped;
import com.pahappa.models.Doctor;
import com.pahappa.models.Appointment;
import com.pahappa.models.Staff;
import com.pahappa.constants.StaffRoles;
import java.util.Date;

@ApplicationScoped
public class BillingServiceImpl implements BillingService {
    private final BillingDao billingDao = new BillingDao();

    // Billing operations
    @Override
    public Billing createBilling(Patient patient, double amount, String description) {
        Billing billing = new Billing(
                patient,
                new Date(),
                amount,
                BillingStatus.PENDING,
                false
        );
        billing.setServiceDescription(description);
        return billingDao.saveBilling(billing);
    }

    @Override
    public List<Billing> getAllBillings() {
        return billingDao.getAllBillings();
    }

    @Override
    public Billing getBillingById(Long id) {
        return billingDao.getBillingById(id);
    }

    @Override
    public List<Billing> getBillingsByPatient(Long patientId) {
        return billingDao.getBillingsByPatient(patientId);
    }

    @Override
    public void updateBilling(Billing billing) {
        billingDao.updateBilling(billing);
    }

    @Override
    public void processPayment(Billing billing, String paymentMethod) {
        Billing managedBilling = billingDao.getBillingById(billing.getId());
        if (managedBilling != null) {
            managedBilling.setPaymentMethod(paymentMethod);
            managedBilling.setStatus(BillingStatus.PAID);
            billingDao.updateBilling(managedBilling);
        }
    }

    @Override
    public void deleteBilling(Long id) {
        billingDao.deleteBilling(id);
    }

    @Override
    public void softDeleteBilling(Long id) {
        billingDao.softDeleteBilling(id);
    }

    @Override
    public void restoreBilling(Long id) {
        billingDao.restoreBilling(id);
    }

    public List<Billing> getDeletedBillings() {
        return billingDao.getDeletedBillings();
    }
}