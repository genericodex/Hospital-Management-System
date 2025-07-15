package com.pahappa.services.patient;

import com.pahappa.models.Patient;
import com.pahappa.models.Staff;

import java.util.Date;
import java.util.List;

public interface PatientService {
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
    long countActivePatients();
}
