package com.pahappa.services;

import com.pahappa.dao.DoctorDao;
import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.Doctor;
import com.pahappa.models.Staff;
import com.pahappa.models.AuditLog;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Date;
import com.pahappa.models.Patient;
import com.pahappa.models.Appointment;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DoctorServiceImpl implements HospitalService {
    private final DoctorDao doctorDao = new DoctorDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    // Doctor operations
    @Override
    public Doctor createDoctor(String firstName, String lastName, String specialization, String contactNumber, String email, Boolean isDeleted, String password) {
        Doctor doctor = new Doctor(firstName, lastName, specialization, contactNumber, email, isDeleted, password);
        doctorDao.saveDoctor(doctor);
        AuditLog log = new AuditLog();
        log.setActionType("CREATE");
        log.setEntityName("Doctor");
        log.setEntityId(doctor.getId() != null ? doctor.getId().toString() : "N/A");
        log.setOldValue(null);
        log.setNewValue(doctor.toString());
        log.setTimestamp(LocalDateTime.now());
        auditLogDao.saveAuditLog(log);
        return doctor;
    }
    @Override
    public Doctor getDoctorById(Long id) { return doctorDao.getDoctorById(id); }
    @Override
    public List<Doctor> getAllDoctors() { return doctorDao.getAllDoctors(); }
    @Override
    public List<Doctor> getAllActiveDoctors() { return doctorDao.getAllActiveDoctors(); }
    @Override
    public List<Doctor> getDeletedDoctors() { return doctorDao.getDeletedDoctors(); }
    @Override
    public void updateDoctor(Doctor doctor, Staff staff) {
        Doctor old = doctorDao.getDoctorById(doctor.getId());
        doctorDao.updateDoctor(doctor);
        AuditLog log = new AuditLog();
        log.setActionType("UPDATE");
        log.setEntityName("Doctor");
        log.setEntityId(doctor.getId() != null ? doctor.getId().toString() : "N/A");
        log.setOldValue(old != null ? old.toString() : null);
        log.setNewValue(doctor.toString());
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
    }
    @Override
    public void deleteDoctor(Long id, Staff staff) {
        Doctor old = doctorDao.getDoctorById(id);
        doctorDao.deleteDoctor(id);
        AuditLog log = new AuditLog();
        log.setActionType("DELETE");
        log.setEntityName("Doctor");
        log.setEntityId(id != null ? id.toString() : "N/A");
        log.setOldValue(old != null ? old.toString() : null);
        log.setNewValue(null);
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
    }
    @Override
    public void softDeleteDoctor(Long id, Staff staff) {
        Doctor old = doctorDao.getDoctorById(id);
        doctorDao.softDeleteDoctor(id);
        AuditLog log = new AuditLog();
        log.setActionType("SOFT_DELETE");
        log.setEntityName("Doctor");
        log.setEntityId(id != null ? id.toString() : "N/A");
        log.setOldValue(old != null ? old.toString() : null);
        log.setNewValue(null);
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
    }
    @Override
    public void restoreDoctor(Long id, Staff staff) {
        Doctor old = doctorDao.getDoctorById(id);
        doctorDao.restoreDoctor(id);
        AuditLog log = new AuditLog();
        log.setActionType("RESTORE");
        log.setEntityName("Doctor");
        log.setEntityId(id != null ? id.toString() : "N/A");
        log.setOldValue(old != null ? old.toString() : null);
        log.setNewValue(null);
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
    }

    // Stub implementations for other HospitalService methods
    @Override public Patient createPatient(String a, String b, Date c, String d, String e, String f, Boolean g, String i,Staff h) { throw new UnsupportedOperationException(); }
    @Override public Patient getPatientById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Patient> getAllPatients() { throw new UnsupportedOperationException(); }
    @Override public void updatePatient(Patient a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void deletePatient(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void softDeletePatient(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public void restorePatient(Long a, Staff b) { throw new UnsupportedOperationException(); }
    @Override public List<Patient> getAllActivePatient() { throw new UnsupportedOperationException(); }
    @Override public List<Patient> getDeletedPatient() { throw new UnsupportedOperationException(); }
    @Override public Appointment createAppointment(Patient a, Doctor b, Date c, String d, Boolean e) { throw new UnsupportedOperationException(); }
    @Override public List<Appointment> getAllAppointments() { throw new UnsupportedOperationException(); }
    @Override public Appointment getAppointmentById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Appointment> getAppointmentsByDoctor(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Appointment> getAppointmentsByPatient(Long id) { throw new UnsupportedOperationException(); }
    @Override public void updateAppointment(Appointment a) { throw new UnsupportedOperationException(); }
    @Override public void cancelAppointment(Long a) { throw new UnsupportedOperationException(); }
    @Override public void deleteAppointment(Long a) { throw new UnsupportedOperationException(); }
    @Override public void restoreAppointment(Long a) { throw new UnsupportedOperationException(); }
    @Override public List<Appointment> getAllActiveAppointments() { throw new UnsupportedOperationException(); }
    @Override public List<Appointment> getDeletedAppointments() { throw new UnsupportedOperationException(); }
    // Add similar stubs for Staff and Billing if needed
    @Override
    public com.pahappa.models.Staff createStaff(String a, String b, String c, String d, com.pahappa.constants.StaffRoles e, String f, Date g, String h, Boolean i, com.pahappa.models.Staff j) { return null; }
    @Override
    public List<com.pahappa.models.Staff> getAllStaff() { return java.util.Collections.emptyList(); }
    @Override
    public com.pahappa.models.Staff getStaffById(Long id) { return null; }
    @Override
    public com.pahappa.models.Staff authenticateStaff(String a, String b) { return null; }
    @Override
    public List<com.pahappa.models.Staff> getAllActiveStaff() { return java.util.Collections.emptyList(); }
    @Override
    public List<com.pahappa.models.Staff> getDeletedStaff() { return java.util.Collections.emptyList(); }
    @Override
    public void updateStaff(com.pahappa.models.Staff a, com.pahappa.models.Staff b) { /* no-op */ }
    @Override
    public void deleteStaff(Long a, com.pahappa.models.Staff b) { /* no-op */ }
    @Override
    public void softDeleteStaff(Long a, com.pahappa.models.Staff b) { /* no-op */ }
    @Override
    public void restoreStaff(Long a, com.pahappa.models.Staff b) { /* no-op */ }
    @Override
    public Staff getStaffByEmail(String email) { return null; }
    @Override
    public com.pahappa.models.Billing createBilling(Patient a, double b, String c) { return null; }
    @Override
    public List<com.pahappa.models.Billing> getAllBillings() { return java.util.Collections.emptyList(); }
    @Override
    public com.pahappa.models.Billing getBillingById(Long id) { return null; }
    @Override
    public List<com.pahappa.models.Billing> getBillingsByPatient(Long id) { return java.util.Collections.emptyList(); }
    @Override
    public void updateBilling(com.pahappa.models.Billing a) { /* no-op */ }
    @Override
    public void processPayment(com.pahappa.models.Billing a, String b) { /* no-op */ }
    @Override
    public void deleteBilling(Long a) { /* no-op */ }
}

