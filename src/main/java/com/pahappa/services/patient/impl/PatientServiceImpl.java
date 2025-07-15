package com.pahappa.services.patient.impl;

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

import com.pahappa.services.patient.PatientService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class PatientServiceImpl implements PatientService {
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
    @Override
    public long countActivePatients() {
        return patientDao.countActivePatients();
    }
}