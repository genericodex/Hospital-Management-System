package com.pahappa.services;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.constants.BillingStatus;
import com.pahappa.constants.Specialization;
import com.pahappa.models.*;
import java.util.Date;
import java.util.List;

public interface HospitalService {
    // Patient operations
    Patient createPatient(String firstName, String lastName, Date dob, String contact, String address, String email, Boolean isDeleted, String medicalHistory, Staff staff);
    Patient getPatientById(Long id);
    List<Patient> getAllPatients();
    void updatePatient(Patient patient, Staff staff);
    void deletePatient(Long id, Staff staff);
    void softDeletePatient(Long id, Staff staff);
    void restorePatient(Long id, Staff staff);
    List<Patient> getAllActivePatient();
    List<Patient> getDeletedPatient();

    // Doctor operations
    Doctor createDoctor(String firstName, String lastName, String specialization, String contactNumber, String email, Boolean isDeleted, String password);
    Doctor getDoctorById(Long id);
    List<Doctor> getAllDoctors();
    List<Doctor> getAllActiveDoctors();
    List<Doctor> getDeletedDoctors();
    void updateDoctor(Doctor doctor, Staff staff);
    void deleteDoctor(Long id, Staff staff);
    void softDeleteDoctor(Long id, Staff staff);
    void restoreDoctor(Long id, Staff staff);

    // Appointment operations
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

    // Staff operations
    Staff createStaff(String firstName, String lastName, String email, String contact, Role role, String department, Date hireDate, String password, Boolean isDeleted, Staff performedBy);
    List<Staff> getAllStaff();
    Staff getStaffById(Long id);
    Staff authenticateStaff(String email, String password);
    List<Staff> getAllActiveStaff();
    List<Staff> getDeletedStaff();
    void updateStaff(Staff staff, Staff performedBy);
    void deleteStaff(Long id, Staff performedBy);
    void softDeleteStaff(Long id, Staff performedBy);
    void restoreStaff(Long id, Staff performedBy);
    Staff getStaffByEmail(String mail);

    // billing operations
    Billing createBilling(Patient patient, double amount, String description);
    List<Billing> getAllBillings();
    Billing getBillingById(Long id);
    List<Billing> getBillingsByPatient(Long patientId);
    void updateBilling(Billing billing);
    void processPayment(Billing billing, String paymentMethod);
    void deleteBilling(Long id);

}
