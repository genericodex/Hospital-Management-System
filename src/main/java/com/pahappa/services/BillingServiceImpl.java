package com.pahappa.services;

import com.pahappa.constants.BillingStatus;
import com.pahappa.dao.BillingDao;
import com.pahappa.models.Billing;
import com.pahappa.models.Patient;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import com.pahappa.models.Doctor;
import com.pahappa.models.Appointment;
import com.pahappa.models.Staff;
import com.pahappa.constants.StaffRoles;
import java.util.Date;

@ApplicationScoped
public class BillingServiceImpl implements HospitalService {
    private final BillingDao billingDao = new BillingDao();

    // Billing operations
    @Override
    public Billing createBilling(Patient patient, double amount, String description) {
        Billing billing = new Billing(
            patient,
            new Date(),
            amount,
            BillingStatus.PENDING,
            false
        );
        billing.setServiceDescription(description);
        billingDao.saveBilling(billing);
        return billing;
    }
    @Override
    public List<Billing> getAllBillings() { return billingDao.getAllBillings(); }
    @Override
    public Billing getBillingById(Long id) { return billingDao.getBillingById(id); }
    @Override
    public List<Billing> getBillingsByPatient(Long patientId) { return billingDao.getBillingsByPatient(patientId); }
    @Override
    public void updateBilling(Billing billing) { billingDao.updateBilling(billing); }
    @Override
    public void processPayment(Billing billing, String paymentMethod) {Billing managedBilling = billingDao.getBillingById(billing.getId());
        if (managedBilling != null) {
            managedBilling.setPaymentMethod(paymentMethod);
            managedBilling.setStatus(BillingStatus.PAID);
            billingDao.updateBilling(managedBilling);
        } }
    @Override
    public void deleteBilling(Long id) { billingDao.deleteBilling(id); }
    public void softDeleteBilling(Long id) { billingDao.softDeleteBilling(id); }
    public void restoreBilling(Long id) { billingDao.restoreBilling(id); }
    public List<Billing> getDeletedBillings() { return billingDao.getDeletedBillings(); }

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
    @Override public Staff createStaff(String a, String b, String c, String d, StaffRoles e, String f, Date g, String h, Boolean i, Staff j) { throw new UnsupportedOperationException(); }
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
