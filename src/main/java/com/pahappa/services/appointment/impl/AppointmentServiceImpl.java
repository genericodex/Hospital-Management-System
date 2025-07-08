package com.pahappa.services.appointment.impl;

import com.pahappa.dao.AppointmentDao;
import com.pahappa.models.Appointment;
import com.pahappa.models.Patient;
import com.pahappa.models.Doctor;
import java.util.Date;
import java.util.List;

import com.pahappa.services.HospitalService;
import com.pahappa.services.appointment.AppointmentService;
import jakarta.enterprise.context.ApplicationScoped;
import com.pahappa.models.Staff;
import com.pahappa.models.Billing;

@ApplicationScoped
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentDao appointmentDao = new AppointmentDao();

    // Appointment operations
    @Override
    public Appointment createAppointment(Patient patient, Doctor doctor, Date appointmentTime, String reason, Boolean isDeleted) {
        Appointment appointment = new Appointment(patient, doctor, appointmentTime, com.pahappa.constants.AppointmentStatus.SCHEDULED, isDeleted);
        appointment.setReasonForVisit(reason);
        return appointmentDao.saveAppointment(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDao.getAllAppointments();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentDao.getAppointmentById(id);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentDao.getAppointmentsByDoctor(doctorId);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentDao.getAppointmentsByPatient(patientId);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        appointmentDao.updateAppointment(appointment);
    }

    @Override
    public void cancelAppointment(Long id) {
        appointmentDao.softDeleteAppointment(id);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentDao.deleteAppointment(id);
    }

    @Override
    public void restoreAppointment(Long id) {
        appointmentDao.restoreAppointment(id);
    }

    @Override
    public List<Appointment> getAllActiveAppointments() {
        return appointmentDao.getAllActiveAppointments();
    }

    @Override
    public List<Appointment> getDeletedAppointments() {
        return appointmentDao.getDeletedAppointments();
    }

}