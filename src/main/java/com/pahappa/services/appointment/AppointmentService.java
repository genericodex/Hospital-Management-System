package com.pahappa.services.appointment;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    void createAppointment(Appointment appointment);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    List<Appointment> getAppointmentsByDoctor(Long doctorId);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    void updateAppointment(Appointment appointment);
    void cancelAppointment(Long id);
    void deleteAppointment(Long id);
    void restoreAppointment(Long id);
    List<Appointment> getAllActiveAppointments();
    List<Appointment> getDeletedAppointments();
    List<Appointment> getDeletedAppointmentsByDoctor(Long doctorId);
    List<Appointment> getFilteredAppointments(Long patientId, Long doctorId, LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Appointment> findRecentAppointments(int limit);
    Map<LocalDate, Long> getDailyAppointmentCounts(LocalDate startDate, LocalDate endDate);
    Map<AppointmentStatus, Long> getGlobalAppointmentStatusCounts();
    long countActiveAppointments();
    List<Object[]> getAppointmentCountsByDoctor();
    List<Appointment> getTodaysAppointmentsForDoctor(Long doctorId);
    List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId);
    Appointment findById(Long id); // Add this line
    void updateCalendarAppointment(Appointment appointment);
    Map<LocalDate, Long> getDailyAppointmentCountsForDoctor(Long doctorId, LocalDate startDate, LocalDate endDate);
    List<Object[]> getDailyAppointmentCountsByStatus(LocalDate startDate, LocalDate endDate);
    List<Object[]> getDailyAppointmentCountsByStatusForDoctor(Long doctorId, LocalDate startDate, LocalDate endDate);
}
