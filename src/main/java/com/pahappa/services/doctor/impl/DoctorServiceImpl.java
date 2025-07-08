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
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DoctorServiceImpl implements DoctorService {
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
}

