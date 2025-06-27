package com.pahappa.dao;

import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class BillingDao {

    // Create
    public void saveBilling(Billing billing) {
        // Null check for referenced Patient
        Long patientId = billing.getPatient() != null ? billing.getPatient().getId() : null;
        if (patientId == null) {
            throw new IllegalArgumentException("Patient must not be null and must have an ID.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Billing newBilling = new Billing();
        Patient managedPatient = session.get(Patient.class, patientId);
        newBilling.setPatient(managedPatient);
        newBilling.setBillDate(billing.getBillDate());
        newBilling.setAmount(billing.getAmount());
        newBilling.setServiceDescription(billing.getServiceDescription());
        newBilling.setStatus(billing.getStatus());
        newBilling.setPaymentMethod(billing.getPaymentMethod());
        session.save(newBilling);
    }

    // Read
    public Billing getBillingById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Billing id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.get(Billing.class, id);
    }

    public List<Billing> getAllBillings() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery("FROM Billing", Billing.class).list();
    }

    // Update
    public void updateBilling(Billing billing) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Billing managedBilling = session.get(Billing.class, billing.getId());
        if (managedBilling != null) {
            Long patientId = billing.getPatient() != null ? billing.getPatient().getId() : null;
            if (patientId == null) {
                throw new IllegalArgumentException("Patient must not be null and must have an ID.");
            }
            Patient managedPatient = session.get(Patient.class, patientId);
            managedBilling.setPatient(managedPatient);
            managedBilling.setBillDate(billing.getBillDate());
            managedBilling.setAmount(billing.getAmount());
            managedBilling.setServiceDescription(billing.getServiceDescription());
            managedBilling.setStatus(billing.getStatus());
            managedBilling.setPaymentMethod(billing.getPaymentMethod());
            session.update(managedBilling);
        }
    }

    // Delete
    public void deleteBilling(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Billing id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Billing billing = session.get(Billing.class, id);
        if (billing != null) session.delete(billing);
    }

    // Special Queries
    public List<Billing> getBillingsByPatient(Long patientId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                        "FROM Billing WHERE patient.id = :patientId", Billing.class)
                .setParameter("patientId", patientId)
                .list();
    }
}