package com.pahappa.dao;
import com.pahappa.models.Staff;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;


/**
 * @Session is the main runtime interface between Java and Hibernate
 * <p>
 *     - 'beginTransaction()': Starts a new transaction
 * <p>
 *     - 'commit()': Saves changes permanently
 * <p>
 *     - 'rollback()': Reverts changes if something goes wrong
 *
 * @ApplicationScoped: This is a Jakarta EE (CDI) annotation. It tells the application server:
 * "Create exactly one instance of this DAO for the entire application, and reuse it whenever I ask for it."
 * This tells the server to create only one single instance of this PatientDao for the entire application.
 * It's super efficient because everyone shares the same object, like a single librarian for a whole library.
 *
 * @Serializable : A marker interface, for  simply flagging it and telling the Java Virtual Machine (JVM):
 * <p>
 *
 * This is like giving the PatientDao object a "passport." It allows the application server to save it to
 * disk or send it across a network (for server clusters) if needed. It's a requirement for
 * ApplicationScoped beans.
 *
 *
 */
@ApplicationScoped
public class PatientDao implements Serializable {

    /**
     * getSessionFactory() returns a special object that creates and manages connections to the database.
     * Think of it as a factory that produces sessions (connections).
     * <p>
     *
     * getCurrentSession() gives you the current active connection (session) to the database for your ongoing work.
     * If there isn’t one, it creates a new one for you. This session is used to read from or write to the database.
     */
    private static final Logger logger = LoggerFactory.getLogger(PatientDao.class);

    // Create

    /**
     * @persist(object) takes a transient (new) object and makes it persistent.
     * It tells Hibernate, "I want to save this new object to the database."
     */
    public void savePatient(Patient patient) {
        logger.debug("Attempting to save patient: {}", patient.getEmail());
        // Persist patient without strict null checks; validation should be handled in service or JSF layer
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(patient);
        logger.info("Successfully saved patient with ID: {}", patient.getId());
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

    /**
     * @merge(object) takes a detached object (like one coming from a JSF form)
     * and synchronizes its state with the database, by loading the current version
     * of that patient from the database into the session. It then copies the state
     * (all the fields) from your detached patient object onto the initialized object,
     * which is then returned as a persistent and watched object in Hibernate
     */
    public void updatePatient(Patient patient) {
        // Persist patient without strict null checks; validation should be handled in service or JSF layer
        if (patient.getId() == null) {
            throw new IllegalArgumentException("Patient id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient managedPatient = session.get(Patient.class, patient.getId());
        if (managedPatient != null) {
            managedPatient.setFirstName(patient.getFirstName());
            managedPatient.setLastName(patient.getLastName());
            managedPatient.setDateOfBirth(patient.getDateOfBirth());
            managedPatient.setContactNumber(patient.getContactNumber());
            managedPatient.setAddress(patient.getAddress());
            managedPatient.setMedicalHistory(patient.getMedicalHistory());
            managedPatient.setEmail(patient.getEmail());
            managedPatient.setDeleted(patient.isDeleted());
            managedPatient.setUpdatedBy(patient.getUpdatedBy());
            managedPatient.setUpdatedAt(patient.getUpdatedAt());
            session.merge(managedPatient);
        }
    }

    // Delete

    /**
     * @remove(object) takes a persistent (managed) object
     * and marks it for deletion from the database.
     * The actual DELETE SQL statement is typically executed when the transaction commits.
     */
    public void deletePatient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient id must not be null.");
        }
        logger.debug("Attempting to delete patient with ID: {}", id);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Patient patient = session.get(Patient.class, id);
        if (patient != null) {
            session.remove(patient);
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
            session.merge(patient);
        }
    }

    // Get all non-deleted staff
    public List<Patient> getAllActivePatient() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Patient WHERE isDeleted = false", Patient.class).list();
    }

    /**
     * @.uniqueResult() is used to make certain that the query will find at most one result.
     */
    public long countActivePatients() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String hql = "SELECT count(p.id) FROM Patient p WHERE p.isDeleted = false";
        Long count = session.createQuery(hql, Long.class).uniqueResult();
        return count != null ? count : 0L; // Return 0 if the result is null
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
            session.merge(patient);
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