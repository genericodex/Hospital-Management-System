package com.pahappa.services;

import com.pahappa.dao.PatientDao;
import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.Patient;
import com.pahappa.models.Staff;
import com.pahappa.models.AuditLog;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Date;
import com.pahappa.models.Doctor;
import com.pahappa.models.Appointment;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientServiceImpl implements HospitalService {
    private final PatientDao patientDao = new PatientDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    // Patient operations
    @Override
    public Patient createPatient(String firstName, String lastName, Date dob, String contact, String address, String email, Boolean isDeleted,String medicalHistory, Staff staff) {
        Patient patient = new Patient(firstName, lastName, dob, contact, address, email, isDeleted ,medicalHistory);
        patientDao.savePatient(patient);
        AuditLog log = new AuditLog();
        log.setActionType("CREATE");
        log.setEntityName("Patient");
        log.setEntityId(patient.getId() != null ? patient.getId().toString() : "N/A");
        log.setOldValue(null);
        log.setNewValue(patient.toString());
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
        return patient;
    }
    @Override
    public Patient getPatientById(Long id) { return patientDao.getPatientById(id); }
    @Override
    public List<Patient> getAllPatients() { return patientDao.getAllPatients(); }
    @Override
    public void updatePatient(Patient patient, Staff staff) {
        Patient old = patientDao.getPatientById(patient.getId());
        patientDao.updatePatient(patient);
        AuditLog log = new AuditLog();
        log.setActionType("UPDATE");
        log.setEntityName("Patient");
        log.setEntityId(patient.getId() != null ? patient.getId().toString() : "N/A");
        log.setOldValue(old != null ? old.toString() : null);
        log.setNewValue(patient.toString());
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
    }
    @Override
    public void deletePatient(Long id, Staff staff) {
        Patient old = patientDao.getPatientById(id);
        patientDao.deletePatient(id);
        AuditLog log = new AuditLog();
        log.setActionType("DELETE");
        log.setEntityName("Patient");
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
    public void softDeletePatient(Long id, Staff staff) {
        Patient old = patientDao.getPatientById(id);
        patientDao.softDeletePatient(id);
        AuditLog log = new AuditLog();
        log.setActionType("SOFT_DELETE");
        log.setEntityName("Patient");
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
    public void restorePatient(Long id, Staff staff) {
        Patient old = patientDao.getPatientById(id);
        patientDao.restorePatient(id);
        AuditLog log = new AuditLog();
        log.setActionType("RESTORE");
        log.setEntityName("Patient");
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
    public List<Patient> getAllActivePatient() { return patientDao.getAllActivePatient(); }
    @Override
    public List<Patient> getDeletedPatient() { return patientDao.getDeletedPatients(); }



    // Doctor operations (return null or empty for unsupported, but do not throw)
    @Override
    public Doctor createDoctor(String a, String b, String c, String d, String e, Boolean f, String g) { return null; }
    @Override
    public Doctor getDoctorById(Long id) { return null; }
    @Override
    public List<Doctor> getAllDoctors() { return java.util.Collections.emptyList(); }
    @Override
    public List<Doctor> getAllActiveDoctors() { return java.util.Collections.emptyList(); }
    @Override
    public List<Doctor> getDeletedDoctors() { return java.util.Collections.emptyList(); }
    @Override
    public void updateDoctor(Doctor a, Staff b) { /* no-op */ }
    @Override
    public void deleteDoctor(Long a, Staff b) { /* no-op */ }
    @Override
    public void softDeleteDoctor(Long a, Staff b) { /* no-op */ }
    @Override
    public void restoreDoctor(Long a, Staff b) { /* no-op */ }
    // Appointment operations
    @Override
    public Appointment createAppointment(Patient a, Doctor b, Date c, String d, Boolean e) { return null; }
    @Override
    public List<Appointment> getAllAppointments() { return java.util.Collections.emptyList(); }
    @Override
    public Appointment getAppointmentById(Long id) { return null; }
    @Override
    public List<Appointment> getAppointmentsByDoctor(Long id) { return java.util.Collections.emptyList(); }
    @Override
    public List<Appointment> getAppointmentsByPatient(Long id) { return java.util.Collections.emptyList(); }
    @Override
    public void updateAppointment(Appointment a) { /* no-op */ }
    @Override
    public void cancelAppointment(Long a) { /* no-op */ }
    @Override
    public void deleteAppointment(Long a) { /* no-op */ }
    @Override
    public void restoreAppointment(Long a) { /* no-op */ }
    @Override
    public List<Appointment> getAllActiveAppointments() { return java.util.Collections.emptyList(); }
    @Override
    public List<Appointment> getDeletedAppointments() { return java.util.Collections.emptyList(); }
    // Staff and Billing operations
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
