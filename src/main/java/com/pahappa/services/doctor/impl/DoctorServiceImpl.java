package com.pahappa.services.doctor.impl;

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

import com.pahappa.services.HospitalService;
import com.pahappa.services.doctor.DoctorService;
import com.pahappa.util.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@Named
@ApplicationScoped
public class DoctorServiceImpl implements DoctorService {
    // --- FIX: Use CDI for dependency injection ---
    @Inject
    private DoctorDao doctorDao;

    @Inject
    private AuditLogDao auditLogDao;

    @Inject
    private PasswordEncoder passwordEncoder;
    // --- END OF FIX ---


    // Doctor operations
    @Override
    public Doctor createDoctor(String firstName, String lastName, String specialization, String contactNumber, String email, Boolean isDeleted, String password) {
        // --- FIX: Hash the password before creating the doctor ---
        String hashedPassword = passwordEncoder.encode(password);
        Doctor doctor = new Doctor(firstName, lastName, specialization, contactNumber, email, isDeleted, hashedPassword);
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
    public Doctor getDoctorById(Long id) {
        return doctorDao.getDoctorById(id);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorDao.getAllDoctors();
    }

    @Override
    public List<Doctor> getAllActiveDoctors() {
        return doctorDao.getAllActiveDoctors();
    }

    @Override
    public List<Doctor> getDeletedDoctors() {
        return doctorDao.getDeletedDoctors();
    }

    @Override
    public void updateDoctor(Doctor doctor, Staff staff) {
        Doctor old = doctorDao.getDoctorById(doctor.getId());
        if (old == null) {
            throw new IllegalStateException("Cannot update a doctor that does not exist.");
        }
        // --- FIX: Check if the password was changed and needs re-hashing ---
        // This handles updates from the admin panel.
        // This logic correctly handles all cases:
        if (doctor.getPassword() != null && !doctor.getPassword().isEmpty()) {
            // If a password came from the form, check if it's plain text.
            if (!doctor.getPassword().startsWith("$2a")) { // BCrypt hashes start with $2a, $2b, etc.
                // It's a new, plain-text password. Hash it.
                String hashedNewPassword = passwordEncoder.encode(doctor.getPassword());
                doctor.setPassword(hashedNewPassword);
            }
            // If it already started with "$2a", we assume it's the original hash and do nothing.
        } else {
            // No password came from the form, so we MUST restore the original hash from the database
            // to prevent it from being overwritten with null.
            doctor.setPassword(old.getPassword());
        }
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
    @Transactional
    public void updateDoctor(Doctor doctor) {
        // This is the self-service update. No audit log actor is needed.
        // We can add more complex validation here in the future if needed.
        doctorDao.updateDoctor(doctor);
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

    // --- FIX: Implement the new changePassword method ---
    @Override
    @Transactional // Ensures the operation is atomic
    public void changePassword(Long doctorId, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Passwords cannot be empty.");
        }

        Doctor doctor = doctorDao.getDoctorById(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found.");
        }

        // 1. Verify the current password is correct
        if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
            throw new IllegalArgumentException("The current password you entered is incorrect.");
        }
        // 2. Hash the new password before saving
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        doctor.setPassword(hashedNewPassword);
        // 3. Save the updated doctor member (no audit actor needed for self-service)
        doctorDao.updateDoctor(doctor);
    }
    @Override
    public Doctor authenticateDoctor(String email, String password) {
        // 1. Find the doctor by their email address.
        Doctor doctor = doctorDao.getDoctorByEmail(email); // We will create this method in the DAO

        // 2. If a doctor is found, check if the provided password matches the stored hash.
        if (doctor != null && passwordEncoder.matches(password, doctor.getPassword())) {
            return doctor; // Login successful
        }
        // 3. If no user is found or the password doesn't match, return null.
        return null;
    }
    @Override
    public long countActiveDoctors() {
        return doctorDao.countActiveDoctors();
    }
}

