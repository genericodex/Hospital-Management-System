package com.pahappa.dao;

import com.pahappa.models.Billing;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class BillingDao {

    // Create
    public void saveBilling(Billing billing) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(billing);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Read
    public Billing getBillingById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Billing.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Billing> getAllBillings() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Billing", Billing.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update
    public void updateBilling(Billing billing) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(billing);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteBilling(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Billing billing = session.get(Billing.class, id);
            if (billing != null) session.delete(billing);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Special Queries
    public List<Billing> getBillingsByPatient(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Billing WHERE patient.id = :patientId", Billing.class)
                    .setParameter("patientId", patientId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}