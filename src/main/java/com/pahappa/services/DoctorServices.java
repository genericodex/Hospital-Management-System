package com.pahappa.services;

import com.pahappa.constants.Specialization;
import com.pahappa.models.Doctor;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.pahappa.util.StringUtil.getIntInput;
import static com.pahappa.util.StringUtil.getStringInput;

public class DoctorServices {
    private static final HospitalService service = new HospitalService();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Doctor operations
    public static void createDoctor() {
        System.out.println("\n=== Create New Doctor ===");
        String firstName, lastName, phone, email, specStr;

        do {firstName = getStringInput("Enter first name: ");
            if (firstName.isEmpty()) {System.out.println("First name cannot be empty. Please try again.");}
        }while (firstName.trim().isEmpty());

        do {lastName = getStringInput("Enter last name: ");
            if (lastName.isEmpty()) {System.out.println("Last name cannot be empty. Please try again.");}
        }while (lastName.trim().isEmpty());
        System.out.println("Available specializations:");
        for (int i = 0; i < Specialization.values().length; i++) {
            Specialization spec = Specialization.values()[i];
            System.out.println("- " + spec);
        }

        do {specStr = getStringInput("Enter specialization: ");
            if (specStr.isEmpty()) {System.out.println("Specialization cannot be empty. Please try again.");
            } else {try {
                    Specialization.valueOf(specStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid specialization. Please enter a valid specialization from the list.");
                    specStr = "";
                }
            }
        }while (specStr.trim().isEmpty());

        Specialization specialization = Specialization.valueOf(specStr.toUpperCase());
        do {
            phone = getStringInput("Enter phone number: ");
            if (phone.isEmpty()) {System.out.println("Phone number cannot be empty. Please try again.");}
        }while (phone.trim().isEmpty());
        do {
            email = getStringInput("Enter email: ");
            if (email.isEmpty()) {System.out.println("Email cannot be empty. Please try again.");}
        }while (email.trim().isEmpty());

        Doctor doctor = service.createDoctor(firstName, lastName, specialization, phone, email);
        System.out.println("Doctor created successfully with ID: " + doctor.getId());
    }

    public static void viewDoctor() {
        System.out.println("\n=== View Doctor ===");
        long id = getIntInput("Enter doctor ID: ");
        Doctor doctor = service.getDoctorById(id);
        if (doctor != null) {
            System.out.println("Doctor Details:");
            System.out.println("Name: " + doctor.getFirstName() + " " + doctor.getLastName());
            System.out.println("Specialization: " + doctor.getSpecialization());
            System.out.println("Phone: " + doctor.getContactNumber());
            System.out.println("Email: " + doctor.getEmail());
        } else {
            System.out.println("Doctor not found.");
        }
    }

    public static void updateDoctor() {
        System.out.println("\n=== Update Doctor ===");
        long id = getIntInput("Enter doctor ID: ");
        Doctor doctor = service.getDoctorById(id);
        if (doctor != null) {
            String input;

            input = getStringInput("Enter new first name (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                doctor.setFirstName(input);
            }

            input = getStringInput("Enter new last name (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                doctor.setLastName(input);
            }

            input = getStringInput("Enter new phone number (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                doctor.setContactNumber(input);
            }

            input = getStringInput("Enter new email (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                doctor.setEmail(input);
            }

            service.updateDoctor(doctor);
            System.out.println("Doctor updated successfully.");
        } else {
            System.out.println("Doctor not found.");
        }

    }

    public static void deleteDoctor() {
        System.out.println("\n=== Delete Doctor ===");
        long id = getIntInput("Enter doctor ID: ");
        service.deleteDoctor(id);
        System.out.println("Doctor deleted successfully.");
    }

    public static void listAllDoctors() {
        System.out.println("\n=== All Doctors ===");
        List<Doctor> doctors = service.getAllDoctors();
        doctors.forEach(d -> System.out.println(
                "ID: " + d.getId() +
                ", Name: Dr. " + d.getFirstName() + " " + d.getLastName() +
                ", Specialization: " + d.getSpecialization()));
    }

}
