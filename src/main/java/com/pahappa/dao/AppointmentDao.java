package com.pahappa.dao;

import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class AppointmentDao {

    // Create
    public Appointment saveAppointment(Appointment appointment) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        // Null checks for referenced entities
        Long patientId = appointment.getPatient() != null ? appointment.getPatient().getId() : null;
        Long doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
        if (patientId == null || doctorId == null) {
            throw new IllegalArgumentException("Patient and Doctor must not be null and must have an ID.");
        }
        Appointment newAppointment = new Appointment();
        Patient managedPatient = session.get(Patient.class, patientId);
        Doctor managedDoctor = session.get(Doctor.class, doctorId);
        newAppointment.setPatient(managedPatient);
        newAppointment.setDoctor(managedDoctor);
        newAppointment.setAppointmentTime(appointment.getAppointmentTime());
        newAppointment.setStatus(appointment.getStatus());
        newAppointment.setReasonForVisit(appointment.getReasonForVisit());
        newAppointment.setDeleted(appointment.isDeleted());
        session.save(newAppointment);
        session.flush(); // Ensure ID is generated
        return newAppointment;
    }

    // Read
    public Appointment getAppointmentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.get(Appointment.class, id);
    }

    public List<Appointment> getAllAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
            "SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor",
            Appointment.class
        ).list();
    }

    // Update
    public void updateAppointment(Appointment appointment) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment managedAppointment = session.get(Appointment.class, appointment.getId());
        if (managedAppointment != null) {
            Long patientId = appointment.getPatient() != null ? appointment.getPatient().getId() : null;
            Long doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : null;
            if (patientId == null || doctorId == null) {
                throw new IllegalArgumentException("Patient and Doctor must not be null and must have an ID.");
            }
            Patient managedPatient = session.get(Patient.class, patientId);
            Doctor managedDoctor = session.get(Doctor.class, doctorId);
            managedAppointment.setPatient(managedPatient);
            managedAppointment.setDoctor(managedDoctor);
            managedAppointment.setAppointmentTime(appointment.getAppointmentTime());
            managedAppointment.setStatus(appointment.getStatus());
            managedAppointment.setReasonForVisit(appointment.getReasonForVisit());
            managedAppointment.setDeleted(appointment.isDeleted());
            session.update(managedAppointment);
        }
    }

    // Delete
    public void deleteAppointment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment appointment = session.get(Appointment.class, id);
        if (appointment != null) session.delete(appointment);
    }


    public void softDeleteAppointment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment appointment = session.get(Appointment.class, id);
        if (appointment != null && !appointment.isDeleted()) {
            appointment.setDeleted(true);
            session.update(appointment);
        }
    }

    // Get all non-deleted appointment
    public List<Appointment> getAllActiveAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
            "SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor WHERE a.isDeleted = false", Appointment.class
        ).list();
    }

    // Get all deleted appointment (bin)
    public List<Appointment> getDeletedAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Appointment WHERE isDeleted = true", Appointment.class).list();
    }

    // Restore appointment
    public void restoreAppointment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment appointment = session.get(Appointment.class, id);
        if (appointment != null && appointment.isDeleted()) {
            appointment.setDeleted(false);
            session.update(appointment);
        }
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
            "SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor WHERE a.doctor.id = :doctorId",
            Appointment.class
        ).setParameter("doctorId", doctorId).list();
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
            "SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor WHERE a.patient.id = :patientId AND a.isDeleted = false",
            Appointment.class
        ).setParameter("patientId", patientId).list();
    }
}
