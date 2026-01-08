package com.pahappa.services.patient.impl;

import com.pahappa.dao.PatientDao;
import com.pahappa.models.Patient;
import com.pahappa.models.Staff;

import java.util.List;
import java.util.Date;

import com.pahappa.services.audit.AuditService;
import com.pahappa.services.audit.impl.AuditServiceImpl;
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
    private AuditService auditService;

    // Patient operations
    @Override
    public Patient createPatient(String firstName, String lastName, Date dob, String contact, String address, String email, Boolean isDeleted,String medicalHistory, Staff staff) {
        Patient patient = new Patient(firstName, lastName, dob, contact, address, email, isDeleted ,medicalHistory);
        // Set audit metadata before saving
        if (staff != null) {
            patient.setCreatedBy(staff.getId());
        }
        patient.setCreatedAt(new Date());

        // 1. Save the patient first to get an ID from the database.
        patientDao.savePatient(patient);

        // 2. Now that the patient has an ID, log the action.
        auditService.log("CREATE", "Patient", patient.getId().toString(), null, patient, staff);
        return patient;
    }
    @Override
    public Patient getPatientById(Long id) { return patientDao.getPatientById(id); }
    @Override
    public List<Patient> getAllPatients() { return patientDao.getAllPatients(); }
    @Override
    public void updatePatient(Patient patient, Staff staff) {
        Patient old = patientDao.getPatientById(patient.getId());
        // 1. Fetch the managed entity to get a true representation of the old state.
        Patient managedOldState = patientDao.getPatientById(patient.getId());
        if (managedOldState == null) {
            throw new IllegalStateException("Patient with ID " + patient.getId() + " not found for update.");
        }

        // 2. Create a clean, detached copy of the old state BEFORE any changes are made.
        Patient oldStateCopy = new Patient(managedOldState);
        // 3. Set audit metadata on the object that is about to be updated.
        if (staff != null) {
            patient.setUpdatedBy(staff.getId());
        }
        patient.setUpdatedAt(new Date());

        // 4. Perform the update.

        patientDao.updatePatient(patient);
        // 5. Log the changes by comparing the clean copy with the updated object from the form.
        auditService.log("UPDATE", "Patient", patient.getId().toString(), oldStateCopy, patient, staff);
    }

    @Override
    public void deletePatient(Long id, Staff staff) {
        Patient oldState = patientDao.getPatientById(id);
        if (oldState != null) {
            patientDao.deletePatient(id);
            auditService.log("DELETE", "Patient", id.toString(), oldState, null, staff);
        }
    }
    @Override
    public void softDeletePatient(Long id, Staff staff) {
        Patient oldState = patientDao.getPatientById(id);
        if (oldState != null) {
            Patient oldStateCopy = new Patient(oldState); // Create snapshot
            patientDao.softDeletePatient(id);
            Patient newState = patientDao.getPatientById(id); // Get new state
            auditService.log("SOFT_DELETE", "Patient", id.toString(), oldStateCopy, newState, staff);
        }
    }

    @Override
    public void restorePatient(Long id, Staff staff) {
        Patient oldState = patientDao.getPatientById(id);
        if (oldState != null) {
            Patient oldStateCopy = new Patient(oldState); // Create snapshot
            patientDao.restorePatient(id);
            Patient newState = patientDao.getPatientById(id); // Get new state
            auditService.log("RESTORE", "Patient", id.toString(), oldStateCopy, newState, staff);
        }
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