package com.pahappa.dao;

import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BillingDao {

    // Create
    public Billing saveBilling(Billing billing) {
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
        session.persist(newBilling);
        return newBilling;
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

        return session.createQuery("FROM Billing b WHERE b.isDeleted = false", Billing.class).list();
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
            session.merge(managedBilling);
        }
    }

    // Delete
    public void deleteBilling(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Billing id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Billing billing = session.get(Billing.class, id);
        if (billing != null) session.remove(billing);
    }

    // Soft Delete
    public void softDeleteBilling(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Billing id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Billing billing = session.get(Billing.class, id);
        if (billing != null && !billing.isDeleted()) {
            billing.setDeleted(true);
            session.merge(billing);
        }
    }

    // Restore
    public void restoreBilling(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Billing id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Billing billing = session.get(Billing.class, id);
        if (billing != null && billing.isDeleted()) {
            billing.setDeleted(false);
            session.merge(billing);
        }
    }

    // Get all deleted billings (bin)
    public List<Billing> getDeletedBillings() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM Billing WHERE isDeleted = true", Billing.class).list();
    }

    // Special Queries
    public List<Billing> getBillingsByPatient(Long patientId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                        "FROM Billing WHERE patient.id = :patientId", Billing.class)
                .setParameter("patientId", patientId)
                .list();
    }

    public List<Billing> findBillings(Long patientId, String paymentMethod) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        // Start with a base query
        StringBuilder hql = new StringBuilder("FROM Billing b WHERE b.isDeleted = false");
        Map<String, Object> parameters = new HashMap<>();

        // Dynamically add conditions based on provided filters
        if (patientId != null) {
            hql.append(" AND b.patient.id = :patientId");
            parameters.put("patientId", patientId);
        }

        if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
            hql.append(" AND b.paymentMethod = :paymentMethod");
            parameters.put("paymentMethod", paymentMethod);
        }

        var query = session.createQuery(hql.toString(), Billing.class);

        // Set all the parameters that were added
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.list();
    }

    public Double getTotalRevenue() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String hql = "SELECT SUM(b.amount) FROM Billing b WHERE b.status = 'PAID' AND b.isDeleted = false";

        Double total = session.createQuery(hql, Double.class).getSingleResult();

        // If there are no paid bills, the sum will be null. Return 0.0 instead.
        return total == null ? 0.0 : total;
    }
    /**
     * NEW EFFICIENT CHART METHOD: Gets the total revenue grouped by day for a given date range.
     * This is perfect for a line chart showing revenue trends.
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A Map where the key is the date and the value is the total revenue for that day.
     */
    public Map<LocalDate, Double> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        // Note: The database function to extract date might vary (e.g., DATE(b.billDate) for MySQL)
        // but this HQL `function('date', ...)` is generally portable.
        String hql = "SELECT function('date', b.billDate), SUM(b.amount) " +
                "FROM Billing b " +
                "WHERE b.status = 'PAID' AND b.isDeleted = false AND function('date', b.billDate) BETWEEN :startDate AND :endDate " +
                "GROUP BY function('date', b.billDate) " +
                "ORDER BY function('date', b.billDate)";

        List<Object[]> results = session.createQuery(hql, Object[].class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        // Convert the raw results into a clean Map for the bean
        return results.stream().collect(
                Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(), // HQL date function often returns java.sql.Date
                        row -> (Double) row[1] != null ? (Double) row[1] : 0.0
                )
        );
    }
}