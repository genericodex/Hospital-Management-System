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
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class PatientServiceImpl implements PatientService {
    /**
     *
     I am using Contexts and Dependency Injection (CDI) instead of manual instantiation.
     <p>
     By using @Inject, I am asking the Jakarta EE container to provide me with
     the managed instances of the DAOs, not new ones. This decouples the service from the
     concrete implementation, making my code vastly easier to test and maintain.
     */
    @Inject
    private PatientDao patientDao;
    @Inject
    private AuditLogDao auditLogDao;

    // Patient operations
    @Override
    public Patient createPatient(String firstName, String lastName, Date dob, String contact, String address, String email, Boolean isDeleted,String medicalHistory, Staff staff) {
        Patient patient = new Patient(firstName, lastName, dob, contact, address, email, isDeleted ,medicalHistory);
        patientDao.savePatient(patient);
        createAuditLog("CREATE", patient, null, patient, staff);
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
        createAuditLog("UPDATE", patient, old, patient, staff);
    }
    @Override
    public void deletePatient(Long id, Staff staff) {
        Patient old = patientDao.getPatientById(id);
        patientDao.deletePatient(id);
        createAuditLog("DELETE", old, old, null, staff);
    }
    @Override
    public void softDeletePatient(Long id, Staff staff) {
        Patient old = patientDao.getPatientById(id);
        patientDao.softDeletePatient(id);
        createAuditLog("SOFT_DELETE", old, old, null, staff);
    }
    @Override
    public void restorePatient(Long id, Staff staff) {
        Patient old = patientDao.getPatientById(id);
        patientDao.restorePatient(id);
        // For restore, the "new value" is the restored patient state
        createAuditLog("RESTORE", old, old, old, staff);
    }
    @Override
    public List<Patient> getAllActivePatient() { return patientDao.getAllActivePatient(); }
    @Override
    public List<Patient> getDeletedPatient() { return patientDao.getDeletedPatients(); }
    @Override
    public long countActivePatients() {
        return patientDao.countActivePatients();
    }

    /**
     * A private helper method to encapsulate the creation of audit logs.
     * This reduces code duplication and makes the main service methods cleaner.
     * @param actionType The type of action (e.g., "CREATE", "UPDATE").
     * @param entity The primary entity involved to get its ID.
     * @param oldState The object state before the change.
     * @param newState The object state after the change.
     * @param staff The staff member performing the action.
     */
    private void createAuditLog(String actionType, Patient entity, Object oldState, Object newState, Staff staff) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setEntityName("Patient");
        if (entity != null && entity.getId() != null) {
            log.setEntityId(entity.getId().toString());
        } else {
            log.setEntityId("N/A");
        }
        log.setOldValue(oldState != null ? oldState.toString() : null);
        log.setNewValue(newState != null ? newState.toString() : null);
        log.setTimestamp(LocalDateTime.now());
        if (staff != null) {
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
        }
        auditLogDao.saveAuditLog(log);
    }
}