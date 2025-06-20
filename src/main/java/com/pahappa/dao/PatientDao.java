package com.pahappa.dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;


/**
 * - A Session is the main runtime interface between Java and Hibernate
 *     - 'beginTransaction()': Starts a new transaction
 *     - 'commit()': Saves changes permanently
 *     - 'rollback()': Reverts changes if something goes wrong
 */

public class PatientDao {

    private static final Logger logger = LoggerFactory.getLogger(PatientDao.class);

    // Create
    public void savePatient(Patient patient) {
        logger.debug("Attempting to save patient: {}", patient.getEmail());

        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            session.persist(patient);
            transaction.commit();
            logger.info("Successfully saved patient with ID: {}", patient.getId());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Failed to save patient", e);
            throw new RuntimeException("Failed to save patient", e);
        }finally {
                session.close();
        }}

    // Read
    public Patient getPatientById(Long id) {
        logger.debug("Attempting to retrieve patient with ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Patient patient = session.get(Patient.class, id);
            logger.info("Successfully retrieved staff member: {}", patient.getEmail());
            return patient;
        } catch (Exception e) {
            logger.warn("No patient found with ID: {}", id);
            return null;
        }
    }

    public List<Patient> getAllPatients() {
        logger.debug("Attempting to retrieve all patients");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Patient> patientList =session.createQuery("FROM com.pahappa.models.Patient", Patient.class).list();

        logger.info("Successfully retrieved {} staff members", patientList.size());
            return patientList;
        } catch (Exception e) {
            logger.error("Error retrieving all patients", e);
            throw new RuntimeException("Failed to retrieve patients list", e);
        }
    }

    // Update
    public void updatePatient(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Delete
    public void deletePatient(Long id) {
        logger.debug("Attempting to delete patient with ID: {}", id);
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.delete(patient);
            transaction.commit();
            logger.info("Successfully deleted patient with ID: {}", id);
            }
            else{
                logger.warn("No patient found with ID: {}", id);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.warn("Transaction rolled back for staff delete operation");
            }
            logger.error("Failed to delete patient with ID: {}", id, e);
            throw new RuntimeException("Failed to delete patient", e);

        }
    }

    // Special Queries
    public Patient findPatientByEmail(String email) {
        logger.debug("Attempting to retrieve patient by email: {}", email);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM com.pahappa.models.Patient WHERE email = :email", Patient.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            logger.warn("No patient found with email: {}", email);
            throw new RuntimeException("Failed to retrieve patient", e);
        }
    }
}