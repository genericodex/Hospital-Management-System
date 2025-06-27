package com.pahappa.services;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.constants.BillingStatus;
import com.pahappa.constants.Specialization;
import com.pahappa.constants.StaffRoles;
import com.pahappa.dao.*;
import com.pahappa.models.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class HospitalService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final PatientDao patientDao = new PatientDao();
    private final DoctorDao doctorDao = new DoctorDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final StaffDao staffDao = new StaffDao();
    private final BillingDao billingDao = new BillingDao();

    // Patient operations
    public Patient createPatient(String firstName, String lastName, Date dob,
                                 String contact, String address, String email, Boolean isDeleted) {
//        Transient object initialised
        Patient patient = new Patient(firstName, lastName, dob, contact, address, email, isDeleted);
        patientDao.savePatient(patient);
        return patient;
    }

    public Patient getPatientById(Long id) {
        return patientDao.getPatientById(id);
    }

    public List<Patient> getAllPatients() {
        return patientDao.getAllPatients();
    }

    public void updatePatient(Patient patient) {
        patientDao.updatePatient(patient);
    }



    public void deletePatient(Long id) {
        patientDao.deletePatient(id);
    }

    public void softDeletePatient(Long id) {
        patientDao.softDeletePatient(id);
    }

    public void restorePatient(Long id) {
        patientDao.restorePatient(id);
    }

    public List<Patient> getAllActivePatient() {
        return patientDao.getAllActivePatient();
    }

    public List<Patient> getDeletedPatient() {
        return patientDao.getDeletedPatients();
    }

//    public Patient findPatientByEmail(String email) {
//        return patientDao.findPatientByEmail(email);
//    }

    // Doctor operations
    public Doctor createDoctor(String firstName, String lastName, Specialization specialization,
                               String contact, String email, Boolean isDeleted) {
        Doctor doctor = new Doctor(firstName, lastName, specialization, contact, email, isDeleted);
        doctorDao.saveDoctor(doctor);
        return doctor;
    }

    public Doctor getDoctorById(Long id) {
        return doctorDao.getDoctorById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorDao.getAllDoctors();
    }

    public void updateDoctor(Doctor doctor) {
        doctorDao.updateDoctor(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorDao.deleteDoctor(id);
    }

//    public List<Doctor> findDoctorsBySpecialization(String specialization) {
//        return doctorDao.findBySpecialization(specialization);
//    }


    public void softDeleteDoctor(Long id) {
        doctorDao.softDeleteDoctor(id);
    }

    public void restoreDoctor(Long id) {
        doctorDao.restoreDoctor(id);
    }

    public List<Doctor> getAllActiveDoctor() {
        return doctorDao.getAllActiveDoctors();
    }

    public List<Doctor> getDeletedDoctors() {
        return doctorDao.getDeletedDoctors();
    }

    // Appointment operations
    public Appointment createAppointment(Patient patient, Doctor doctor,
                                         Date appointmentTime, String reason, Boolean isDeleted) {
        Appointment appointment = new Appointment(patient, doctor, appointmentTime, AppointmentStatus.SCHEDULED, isDeleted);
        appointment.setReasonForVisit(reason);
        appointmentDao.saveAppointment(appointment);
        return appointment;
    }


    public List<Appointment> getAllAppointments() {
        return appointmentDao.getAllAppointments();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentDao.getAppointmentById(id);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentDao.getAppointmentsByDoctor(doctorId);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentDao.getAppointmentsByPatient(patientId);
    }

    public void updateAppointment(Appointment appointment) {
        appointmentDao.updateAppointment(appointment);
    }

    public void cancelAppointment(Long id) {
        appointmentDao.softDeleteAppointment(id);
    }

    public void restoreAppointment(Long id) {
        appointmentDao.restoreAppointment(id);
    }

    public List<Appointment> getAllActiveAppointments() {
        return appointmentDao.getAllActiveAppointments();
    }

    public List<Appointment> getDeletedAppointments() {
        return appointmentDao.getDeletedAppointments();
    }

    // Staff operations
    public Staff createStaff(String firstName, String lastName,
                             String email, String contact,
                             StaffRoles role, String department,
                             Date hireDate, String password, Boolean isDeleted) {
        Staff staff = new Staff(firstName, lastName, email, contact, role, department, hireDate, password,isDeleted);
        staffDao.saveStaff(staff);
        return staff;
    }

    public List<Staff> getAllStaff() {
        return staffDao.getAllStaff();
    }


    public Staff getStaffById(Long id) {
        return staffDao.getStaffById(id);
    }

    public Staff authenticateStaff(String email, String password) {
        return staffDao.authenticate(email, password);
    }

    public void updateStaff(Staff staff) {
        staffDao.updateStaff(staff);
    }

    public void deleteStaff(Long id) {
        staffDao.deleteStaff(id);
    }


    public void softDeleteStaff(Long id) {
        staffDao.softDeleteStaff(id);
    }

    public void restoreStaff(Long id) {
        staffDao.restoreStaff(id);
    }

    public List<Staff> getAllActiveStaff() {
        return staffDao.getAllActiveStaff();
    }

    public List<Staff> getDeletedStaff() {
        return staffDao.getDeletedStaff();
    }

    // billing operations
    public Billing createBilling(Patient patient, double amount, String description) {
        Billing billing = new Billing();
        billing.setPatient(patient);
        billing.setBillDate(new Date());
        billing.setAmount(amount);
        billing.setServiceDescription(description);
        billing.setStatus(BillingStatus.PENDING);
        billingDao.saveBilling(billing);
        return billing;
    }

    public List<Billing> getAllBillings() {
        return billingDao.getAllBillings();
    }

    public Billing getBillingById(Long id) {
        return billingDao.getBillingById(id);
    }

    public List<Billing> getBillingsByPatient(Long patientId) {
        return billingDao.getBillingsByPatient(patientId);
    }

//    public void updateBilling(Billing billing) {
//        billingDao.updateBilling(billing);
//    }

    public void processPayment(Billing billing, String paymentMethod) {
        // Always fetch managed entity from DB before updating
        Billing managedBilling = billingDao.getBillingById(billing.getId());
        if (managedBilling != null) {
            managedBilling.setPaymentMethod(paymentMethod);
            managedBilling.setStatus(BillingStatus.PAID);
            billingDao.updateBilling(managedBilling);
        }
    }

    public void deleteBilling(Long id) {
        billingDao.deleteBilling(id);
    }
}