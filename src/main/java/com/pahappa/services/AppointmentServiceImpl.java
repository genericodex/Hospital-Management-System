package com.pahappa.services;

import com.pahappa.dao.AppointmentDao;
import com.pahappa.models.Appointment;
import com.pahappa.models.Patient;
import com.pahappa.models.Doctor;
import java.util.Date;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import com.pahappa.models.Staff;
import com.pahappa.models.Billing;

@ApplicationScoped
public class AppointmentServiceImpl implements HospitalService {
    private final AppointmentDao appointmentDao = new AppointmentDao();

    // Appointment operations
    @Override
    public Appointment createAppointment(Patient patient, Doctor doctor, Date appointmentTime, String reason, Boolean isDeleted) {
        Appointment appointment = new Appointment(patient, doctor, appointmentTime, null, isDeleted);
        appointment.setReasonForVisit(reason);
        appointmentDao.saveAppointment(appointment);
        return appointment;
    }
    @Override
    public List<Appointment> getAllAppointments() { return appointmentDao.getAllAppointments(); }
    @Override
    public Appointment getAppointmentById(Long id) { return appointmentDao.getAppointmentById(id); }
    @Override
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) { return appointmentDao.getAppointmentsByDoctor(doctorId); }
    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) { return appointmentDao.getAppointmentsByPatient(patientId); }
    @Override
    public void updateAppointment(Appointment appointment) { appointmentDao.updateAppointment(appointment); }
    @Override
    public void cancelAppointment(Long id) { appointmentDao.softDeleteAppointment(id); }
    @Override
    public void deleteAppointment(Long id) { appointmentDao.deleteAppointment(id); }
    @Override
    public void restoreAppointment(Long id) { appointmentDao.restoreAppointment(id); }
    @Override
    public List<Appointment> getAllActiveAppointments() { return appointmentDao.getAllActiveAppointments(); }
    @Override
    public List<Appointment> getDeletedAppointments() { return appointmentDao.getDeletedAppointments(); }

    // Stub implementations for other HospitalService methods
    @Override public Patient createPatient(String a, String b, Date c, String d, String e, String f, Boolean g, String i, Staff h) { throw new UnsupportedOperationException(); }
    @Override public Patient getPatientById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Patient> getAllPatients() { throw new UnsupportedOperationException(); }
    @Override public void updatePatient(Patient a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void deletePatient(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void softDeletePatient(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void restorePatient(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public List<Patient> getAllActivePatient() { throw new UnsupportedOperationException(); }
    @Override public List<Patient> getDeletedPatient() { throw new UnsupportedOperationException(); }
    @Override public Doctor createDoctor(String a, String b, String c, String d, String e, Boolean f, String g) { throw new UnsupportedOperationException(); }
    @Override public Doctor getDoctorById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Doctor> getAllDoctors() { throw new UnsupportedOperationException(); }
    @Override public List<Doctor> getAllActiveDoctors() { throw new UnsupportedOperationException(); }
    @Override public List<Doctor> getDeletedDoctors() { throw new UnsupportedOperationException(); }
    @Override public void updateDoctor(Doctor a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void deleteDoctor(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void softDeleteDoctor(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void restoreDoctor(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public Billing createBilling(Patient a, double b, String c) { throw new UnsupportedOperationException(); }
    @Override public List<Billing> getAllBillings() { throw new UnsupportedOperationException(); }
    @Override public Billing getBillingById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Billing> getBillingsByPatient(Long id) { throw new UnsupportedOperationException(); }
    @Override public void updateBilling(Billing a) { throw new UnsupportedOperationException(); }
    @Override public void processPayment(Billing a, String b) { throw new UnsupportedOperationException(); }
    @Override public void deleteBilling(Long a) { throw new UnsupportedOperationException(); }
    @Override public Staff createStaff(String a, String b, String c, String d, com.pahappa.constants.StaffRoles e, String f, Date g, String h, Boolean i, Staff j) { throw new UnsupportedOperationException(); }
    @Override public List<Staff> getAllStaff() { throw new UnsupportedOperationException(); }
    @Override public Staff getStaffById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Staff authenticateStaff(String a, String b) { throw new UnsupportedOperationException(); }
    @Override public List<Staff> getAllActiveStaff() { throw new UnsupportedOperationException(); }
    @Override public List<Staff> getDeletedStaff() { throw new UnsupportedOperationException(); }
    @Override public void updateStaff(Staff a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void deleteStaff(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void softDeleteStaff(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void restoreStaff(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public Staff getStaffByEmail(String email) { return null; }
}

