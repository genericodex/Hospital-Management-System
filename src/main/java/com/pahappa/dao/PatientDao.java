package com.pahappa.dao;
import com.pahappa.models.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
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
        // Null checks for required fields
        if (patient.getFirstName() == null || patient.getLastName() == null || patient.getDateOfBirth() == null || patient.getContactNumber() == null || patient.getAddress() == null || patient.getEmail() == null) {
            throw new IllegalArgumentException("All patient fields must be provided and not null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient newPatient = new Patient();
        newPatient.setFirstName(patient.getFirstName());
        newPatient.setLastName(patient.getLastName());
        newPatient.setDateOfBirth(patient.getDateOfBirth());
        newPatient.setContactNumber(patient.getContactNumber());
        newPatient.setAddress(patient.getAddress());
        newPatient.setEmail(patient.getEmail());
        newPatient.setDeleted(patient.isDeleted());
        session.persist(newPatient);
        logger.info("Successfully saved patient with ID: {}", newPatient.getId());
    }

    // Read
    public Patient getPatientById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient id must not be null.");
        }
        logger.debug("Attempting to retrieve patient with ID: {}", id);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient patient = session.get(Patient.class, id);
        logger.info("Successfully retrieved staff member: {}", patient != null ? patient.getEmail() : null);
        return patient;
    }

    public List<Patient> getAllPatients() {
        logger.debug("Attempting to retrieve all patients");
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Patient> patientList = session.createQuery("FROM com.pahappa.models.Patient", Patient.class).list();
        logger.info("Successfully retrieved {} staff members", patientList.size());
        return patientList;
    }

    // Update
    public void updatePatient(Patient patient) {
        // Null checks for required fields
        if (patient.getId() == null || patient.getFirstName() == null || patient.getLastName() == null || patient.getDateOfBirth() == null || patient.getContactNumber() == null || patient.getAddress() == null || patient.getEmail() == null) {
            throw new IllegalArgumentException("All patient fields and ID must be provided and not null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient managedPatient = session.get(Patient.class, patient.getId());
        if (managedPatient != null) {
            managedPatient.setFirstName(patient.getFirstName());
            managedPatient.setLastName(patient.getLastName());
            managedPatient.setDateOfBirth(patient.getDateOfBirth());
            managedPatient.setContactNumber(patient.getContactNumber());
            managedPatient.setAddress(patient.getAddress());
            managedPatient.setEmail(patient.getEmail());
            managedPatient.setDeleted(patient.isDeleted());
            session.update(managedPatient);
        }
    }

    // Delete
    public void deletePatient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient id must not be null.");
        }
        logger.debug("Attempting to delete patient with ID: {}", id);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient patient = session.get(Patient.class, id);
        if (patient != null) {
            session.delete(patient);
            logger.info("Successfully deleted patient with ID: {}", id);
        } else {
            logger.warn("No patient found with ID: {}", id);
        }
    }

    public void softDeletePatient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient patient = session.get(Patient.class, id);
        if (patient != null && !patient.isDeleted()) {
            patient.setDeleted(true);
            session.update(patient);
        }
    }

    // Get all non-deleted staff
    public List<Patient> getAllActivePatient() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Patient WHERE isDeleted = false", Patient.class).list();
    }

    // Get all deleted patients (bin)
    public List<Patient> getDeletedPatients() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Patient WHERE isDeleted = true", Patient.class).list();
    }

    // Restore staff
    public void restorePatient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient patient = session.get(Patient.class, id);
        if (patient != null && patient.isDeleted()) {
            patient.setDeleted(false);
            session.update(patient);
        }
    }

    // find by email
    public Patient findPatientByEmail(String email) {
        logger.debug("Attempting to retrieve patient by email: {}", email);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery("FROM com.pahappa.models.Patient WHERE email = :email", Patient.class)
                .setParameter("email", email)
                .uniqueResult();
    }
}