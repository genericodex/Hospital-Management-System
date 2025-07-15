package com.pahappa.dao;

import com.pahappa.models.Doctor;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class DoctorDao implements Serializable {

    // Create
    public void saveDoctor(Doctor doctor) {
        // Persist doctor without strict null checks; validation should be handled in service or JSF layer
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(doctor);
    }

    // Read
    public Doctor getDoctorById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Doctor id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.get(Doctor.class, id);
    }

    public List<Doctor> getAllDoctors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery("FROM Doctor", Doctor.class).list();
    }

    // Update
    public void updateDoctor(Doctor doctor) {
        if (doctor.getId() == null || doctor.getFirstName() == null || doctor.getLastName() == null || doctor.getSpecialization() == null || doctor.getContactNumber() == null || doctor.getEmail() == null) {
            throw new IllegalArgumentException("All doctor fields and ID must be provided and not null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            session.merge(doctor);

    }

    // Delete
    public void deleteDoctor(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Doctor id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Doctor doctor = session.get(Doctor.class, id);
        if (doctor != null) session.remove(doctor);
    }

    // Search Queries by doctor specialization
    public List<Doctor> findBySpecialization(String specialization) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                        "FROM Doctor WHERE specialization = :specialization", Doctor.class)
                .setParameter("specialization", specialization)
                .list();
    }


    public void softDeleteDoctor(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Doctor id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Doctor doctor = session.get(Doctor.class, id);
        if (doctor != null && !doctor.isDeleted()) {
            doctor.setDeleted(true);
            session.merge(doctor);
        }
    }

    // Get all non-deleted doctors
    public List<Doctor> getAllActiveDoctors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Doctor WHERE isDeleted = false", Doctor.class).list();
    }

    // Get all deleted doctors (bin)
    public List<Doctor> getDeletedDoctors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Doctor WHERE isDeleted = true", Doctor.class).list();
    }

    // Restore doctors
    public void restoreDoctor(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Doctor id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Doctor doctor = session.get(Doctor.class, id);
        if (doctor != null && doctor.isDeleted()) {
            doctor.setDeleted(false);
            session.merge(doctor);
        }
    }

    public long countActiveDoctors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String hql = "SELECT count(d.id) FROM Doctor d WHERE d.isDeleted = false";
        Long count = session.createQuery(hql, Long.class).uniqueResult();
        return count != null ? count : 0L;
    }

    public Doctor getDoctorByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        return session.createQuery("FROM com.pahappa.models.Doctor WHERE email= :email AND isDeleted = false", Doctor.class)
                                .setParameter("email", email)
                                .uniqueResult();
                   } catch (Exception e) {
                       // It's good practice to log the exception
                        e.printStackTrace();
                       return null;
                    }

    }
    }