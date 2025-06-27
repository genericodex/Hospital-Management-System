package com.pahappa.dao;

import com.pahappa.models.Doctor;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class DoctorDao {

    // Create
    public void saveDoctor(Doctor doctor) {
        // Null checks for required fields
        if (doctor.getFirstName() == null || doctor.getLastName() == null || doctor.getSpecialization() == null || doctor.getContactNumber() == null || doctor.getEmail() == null) {
            throw new IllegalArgumentException("All doctor fields must be provided and not null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Doctor newDoctor = new Doctor();
        newDoctor.setFirstName(doctor.getFirstName());
        newDoctor.setLastName(doctor.getLastName());
        newDoctor.setSpecialization(doctor.getSpecialization());
        newDoctor.setContactNumber(doctor.getContactNumber());
        newDoctor.setEmail(doctor.getEmail());
        newDoctor.setDeleted(doctor.isDeleted());
        session.save(newDoctor);
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
        Doctor managedDoctor = session.get(Doctor.class, doctor.getId());
        if (managedDoctor != null) {
            managedDoctor.setFirstName(doctor.getFirstName());
            managedDoctor.setLastName(doctor.getLastName());
            managedDoctor.setSpecialization(doctor.getSpecialization());
            managedDoctor.setContactNumber(doctor.getContactNumber());
            managedDoctor.setEmail(doctor.getEmail());
            managedDoctor.setDeleted(doctor.isDeleted());
            session.update(managedDoctor);
        }
    }

    // Delete
    public void deleteDoctor(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Doctor id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Doctor doctor = session.get(Doctor.class, id);
        if (doctor != null) session.delete(doctor);
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
            session.update(doctor);
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
            session.update(doctor);
        }
    }
}