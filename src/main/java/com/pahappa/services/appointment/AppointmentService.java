package com.pahappa.services.appointment;

import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;

import java.util.Date;
import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Patient patient, Doctor doctor, Date appointmentTime, String reason, Boolean isDeleted);
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

}
