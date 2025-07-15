package com.pahappa.services.appointment.impl;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.dao.AppointmentDao;
import com.pahappa.models.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.pahappa.services.appointment.AppointmentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class AppointmentServiceImpl implements AppointmentService {

    @Inject
    private AppointmentDao appointmentDao;

    // Appointment operations
    @Override
    public void createAppointment(Appointment appointment) {
        // Set business logic defaults before saving.
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDeleted(false);

        // The DAO will handle the persistence.
        // The saveAppointment method returns the persisted object, but since our
        // service method is void, we don't need to capture it.
        appointmentDao.saveAppointment(appointment);
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
    @Override
    public List<Appointment> getDeletedAppointmentsByDoctor(Long doctorId) {
        return appointmentDao.getDeletedAppointmentsByDoctor(doctorId);
    }

    @Override
    public List<Appointment> getFilteredAppointments(Long patientId, Long doctorId, LocalDateTime startDate, LocalDateTime endDate, String status) {
        return appointmentDao.getFilteredAppointments(patientId, doctorId, startDate, endDate, status);
    }

    @Override
    public List<Appointment> findRecentAppointments(int limit) {
        return appointmentDao.findRecentAppointments(limit);
    }

    @Override
    public Map<LocalDate, Long> getDailyAppointmentCounts(LocalDate startDate, LocalDate endDate) {
        return appointmentDao.getDailyAppointmentCounts(startDate, endDate);
    }

    @Override
    public Map<AppointmentStatus, Long> getGlobalAppointmentStatusCounts() {
        return appointmentDao.getGlobalAppointmentStatusCounts();
    }

    @Override
    public long countActiveAppointments() {
        return appointmentDao.countActiveAppointments();
    }
    @Override
    public List<Object[]> getAppointmentCountsByDoctor() {
        return appointmentDao.getAppointmentCountsByDoctor();
    }

    @Override
    public List<Appointment> getTodaysAppointmentsForDoctor(Long doctorId) {
        return appointmentDao.getTodaysAppointmentsForDoctor(doctorId);
    }
    @Override
    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId) {
        // This now calls the updated DAO method in AppointmentDao
        return appointmentDao.getUpcomingAppointmentsForDoctor(doctorId);
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentDao.findById(id);
    }

    @Override
    public void updateCalendarAppointment(Appointment appointment) {
        appointmentDao.updateCalendarAppointment(appointment);
    }
}