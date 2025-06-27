package com.pahappa.services;

import com.pahappa.models.*;
import com.pahappa.models.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.pahappa.util.StringUtil.*;
import static com.pahappa.util.StringUtil.getDateInput;
import static com.pahappa.util.StringUtil.getIntInput;
import static com.pahappa.util.StringUtil.getStringInput;

public class AppointmentServices {


    private static final HospitalService service = new HospitalService();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Appointment operations
    public static void createAppointment() {
        System.out.println("\n=== Create New Appointment ===");

        // Get patient
        long patientId = getIntInput("Enter patient ID: ");
        Patient patient = service.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        // Get doctor
        List<Doctor> doctors = service.getAllDoctors();
        System.out.println("Available Doctors:");
        doctors.forEach(d -> System.out.println(
                "ID: " + d.getId() +
                        ", Name: Dr. " + d.getFirstName() + " " + d.getLastName()));
        long doctorId = getIntInput("Enter doctor ID: ");
        Doctor doctor = service.getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        Date appointmentDate;
        try {
            String dateStr = getStringInput("Enter appointment date (yyyy-MM-dd): ");
            String timeStr = getStringInput("Enter appointment time (HH:mm): ");
            String dateTimeStr = dateStr + " " + timeStr;
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            appointmentDate = dateTimeFormat.parse(dateTimeStr);

            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = sdf.parse(sdf.format(now));

            if (appointmentDate.before(today)) {
                System.out.println("Appointment date cannot be in the past. Please enter a valid date.");
                appointmentDate = null;
            }

        } catch (ParseException e) {
            System.out.println("Invalid date/time format. Please use 'yyyy-MM-dd HH:mm'.");
            return;
        }
        String reason = getStringInput("Enter reason for visit: ");
        Boolean isDeleted = false;

        Appointment appointment = service.createAppointment(patient, doctor, appointmentDate, reason, isDeleted);
        System.out.println("Appointment created successfully with ID: " + appointment.getId());
    }

    public static void viewAppointment() {
        System.out.println("\n=== View Appointment ===");
        long id = getIntInput("Enter appointment ID: ");
        Appointment appointment = service.getAppointmentById(id);
        if (appointment != null) {
            System.out.println("Appointment Details:");
            System.out.println("Patient: " + appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
            System.out.println("Doctor: Dr. " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
            System.out.println("Date: " + dateFormat.format(appointment.getAppointmentTime()));
            System.out.println("Reason: " + appointment.getReasonForVisit());
            System.out.println("Status: " + appointment.getStatus());
        } else {
            System.out.println("Appointment not found.");
        }
    }

    public static void updateAppointment() {
        System.out.println("\n=== Update Appointment ===");
        long id = getIntInput("Enter appointment ID: ");
        Appointment appointment = service.getAppointmentById(id);
        if (appointment != null) {
            Date newDate = getDateInput("Enter new appointment date (or press enter to skip)");
            if (newDate != null) {
                appointment.setAppointmentTime(newDate);
            }
            String reason = getStringInput("Enter new reason (or press enter to skip): ");
            if (!reason.isEmpty()) {
                appointment.setReasonForVisit(reason);
            }
            service.updateAppointment(appointment);
            System.out.println("Appointment updated successfully.");
        } else {
            System.out.println("Appointment not found.");
        }
    }

    public static void deleteAppointment() {
        System.out.println("\n=== Cancel Appointment ===");
        long id = getIntInput("Enter appointment ID: ");
        service.cancelAppointment(id);
        System.out.println("Appointment cancelled successfully.");
    }


    public static void restoreAppointment() {
        long id = getIntInput("Enter appointment ID to restore: ");
        service.restoreAppointment(id);
        System.out.println("Appointment restored from bin.");
    }

    public static void listAllAppointments() {
        System.out.println("\n=== All Appointments ===");
        long doctorId = getIntInput("Enter doctor ID (or 0 for all appointments): ");
        List<Appointment> appointments;

        if (doctorId > 0) {
            appointments = service.getAppointmentsByDoctor(doctorId);
        } else {
            appointments = service.getAllActiveAppointments();
        }
        if (appointments == null || appointments.isEmpty()) {
            System.out.println("No appointment members found.");
            return;
        }
        appointments.forEach(a -> System.out.println("ID: " + a.getId() +
                ", Patient: " + a.getPatient().getFirstName() + " " + a.getPatient().getLastName() +
                ", Doctor: Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName() +
                ", Date: " + dateFormat.format(a.getAppointmentTime())));
    }
    public static void listDeletedAppointments() {
            List<Appointment> deletedAppointment = service.getDeletedAppointments();
            // display deletedAppointment
            if (deletedAppointment == null || deletedAppointment.isEmpty()) {
                System.out.println("No deleted appointments found.");
                return;
            }
            deletedAppointment.forEach(a -> System.out.println("ID: " + a.getId() +
                    ", Patient: " + a.getPatient().getFirstName() + " " + a.getPatient().getLastName() +
                    ", Doctor: Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName() +
                    ", Date: " + dateFormat.format(a.getAppointmentTime())));
        }
            /**
             * Instead of using this;
             * for (int i = 0; i < appointments.size(); i++) {
             *     Appointment a = appointments.get(i);
             *     System.out.println("ID: " + a.getId() +
             *         ", Patient: " + a.getPatient().getFirstName() + " " + a.getPatient().getLastName() +
             *         ", Doctor: Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName() +
             *         ", Date: " + dateFormat.format(a.getAppointmentTime()));
             * }
             * Am using;
             */

            /**
             * Am only trying to read data (printing appointments)
             * and want to eliminate or dont need positioning of appointments in our list
             */


    public static void listAppointmentsByDoctor() {
                long doctorId = getIntInput("Enter doctor ID: ");
                List<Appointment> appointments = service.getAppointmentsByDoctor(doctorId);
                if (appointments == null || appointments.isEmpty()) {
                    System.out.println("No appointments found for this doctor.");
                    return;
                }
                appointments.forEach(a -> System.out.println("ID: " + a.getId() +
                        ", Patient: " + a.getPatient().getFirstName() + " " + a.getPatient().getLastName() +
                        ", Date: " + dateFormat.format(a.getAppointmentTime())));
            }

    public static void listAppointmentsByPatient() {
        long patientId = getIntInput("Enter patient ID: ");
        List<Appointment> appointments = service.getAppointmentsByPatient(patientId);
        if (appointments == null || appointments.isEmpty()) {
            System.out.println("No appointments found for this patient.");
            return;
        }
        appointments.forEach(a -> System.out.println("ID: " + a.getId() +
                ", Doctor: Dr. " + a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName() +
                ", Date: " + dateFormat.format(a.getAppointmentTime())));
    }
   }

