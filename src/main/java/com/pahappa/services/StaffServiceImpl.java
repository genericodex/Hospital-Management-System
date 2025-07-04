package com.pahappa.services;

import com.pahappa.dao.StaffDao;
import com.pahappa.models.Staff;
import com.pahappa.constants.StaffRoles;
import java.util.Date;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import com.pahappa.models.Patient;
import com.pahappa.models.Doctor;
import com.pahappa.models.Appointment;
import com.pahappa.models.Billing;

@ApplicationScoped
public class StaffServiceImpl implements HospitalService {
    private final StaffDao staffDao = new StaffDao();

    // Staff operations
    @Override
    public Staff createStaff(String firstName, String lastName, String email, String contact, StaffRoles role, String department, Date hireDate, String password, Boolean isDeleted, Staff performedBy) {
        Staff staff = new Staff(firstName, lastName, email, contact, role, department, hireDate, password, isDeleted);
        staffDao.saveStaff(staff);
        return staff;
    }
    @Override
    public List<Staff> getAllStaff() { return staffDao.getAllStaff(); }
    @Override
    public Staff getStaffById(Long id) { return staffDao.getStaffById(id); }
    @Override
    public Staff authenticateStaff(String email, String password) { return staffDao.authenticate(email, password); }
    @Override
    public List<Staff> getAllActiveStaff() { return staffDao.getAllActiveStaff(); }
    @Override
    public List<Staff> getDeletedStaff() { return staffDao.getDeletedStaff(); }
    @Override
    public void updateStaff(Staff staff, Staff performedBy) { staffDao.updateStaff(staff); }
    @Override
    public void deleteStaff(Long id, Staff performedBy) { staffDao.deleteStaff(id); }
    @Override
    public void softDeleteStaff(Long id, Staff performedBy) { staffDao.softDeleteStaff(id); }
    @Override
    public void restoreStaff(Long id, Staff performedBy) { staffDao.restoreStaff(id); }
    @Override
    public Staff getStaffByEmail(String email) { return staffDao.getStaffByEmail(email); }

    // Stub implementations for other HospitalService methods
    @Override public Patient createPatient(String a, String b, Date c, String d, String e, String f, Boolean g, String h, Staff i) { throw new UnsupportedOperationException(); }
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
    @Override public Billing createBilling(Patient a, double b, String c) { throw new UnsupportedOperationException(); }
    @Override public List<Billing> getAllBillings() { throw new UnsupportedOperationException(); }
    @Override public Billing getBillingById(Long id) { throw new UnsupportedOperationException(); }
    @Override public List<Billing> getBillingsByPatient(Long id) { throw new UnsupportedOperationException(); }
    @Override public void updateBilling(Billing a) { throw new UnsupportedOperationException(); }
    @Override public void processPayment(Billing a, String b) { throw new UnsupportedOperationException(); }
    @Override public void deleteBilling(Long a) { throw new UnsupportedOperationException(); }
}

