package com.pahappa.services;

import com.pahappa.models.Patient;
import com.pahappa.services.HospitalService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static com.pahappa.util.StringUtil.*;

public class PatientServices {
    private static final HospitalService service = new HospitalService();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Patient operations
    public static void createPatient() {
        System.out.println("\n=== Create New Patient ===");
        String firstName, lastName, phone, address, email;
        do {
            firstName = getStringInput("Enter first name: ");
            if (firstName.trim().isEmpty()) {
                System.out.println("First name cannot be empty. Please try again.");
            }
        } while (firstName.trim().isEmpty());
        do {
            lastName = getStringInput("Enter last name: ");
            if (lastName.trim().isEmpty()) {
                System.out.println("Last name cannot be empty. Please try again.");
            }
        }while (lastName.trim().isEmpty());
        Date dob = getDateInput("Enter date of birth");
        do {
            phone = getStringInput("Enter phone number: ");
            if (phone.trim().isEmpty()) {
                System.out.println("Phone number cannot be empty. Please try again.");
            }
        }while (phone.trim().isEmpty());
        do {
            address = getStringInput("Enter address: ");
            if (address.trim().isEmpty()) {
                System.out.println("Address cannot be empty. Please try again.");
            }
        }while (address.trim().isEmpty());
        do{
            email = getStringInput("Enter email: ");
            if (email.trim().isEmpty()) {
                System.out.println("Email cannot be empty. Please try again.");
            }
            else if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Invalid email format. Please try again.");
                email = "";
            }
        }while (email.trim().isEmpty());

        Patient patient = service.createPatient(firstName, lastName, dob, phone, address, email);
        System.out.println("Patient created successfully with ID: " + patient.getId());
    }

    public static void viewPatient() {
        System.out.println("\n=== View Patient ===");
        long id = getIntInput("Enter patient ID: ");
        Patient patient = service.getPatientById(id);
        if (patient != null) {
            System.out.println("Patient Details:");
            System.out.println("Name: " + patient.getFirstName() + " " + patient.getLastName());
            System.out.println("DOB: " + dateFormat.format(patient.getDateOfBirth()));
            System.out.println("Phone: " + patient.getContactNumber());
            System.out.println("Address: " + patient.getAddress());
            System.out.println("Email: " + patient.getEmail());
        } else {
            System.out.println("Patient not found.");
        }
    }

    public static void updatePatient() {
        System.out.println("\n=== Update Patient ===");
        long id = getIntInput("Enter patient ID: ");
        Patient patient = service.getPatientById(id);
        if (patient != null) {
            String input;

            input = getStringInput("Enter new first name (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                patient.setFirstName(input);
            }

            input = getStringInput("Enter new last name (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                patient.setLastName(input);
            }

            input = getStringInput("Enter new phone number (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                patient.setContactNumber(input);
            }

            input = getStringInput("Enter new address (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                patient.setAddress(input);
            }

            input = getStringInput("Enter new email (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                patient.setEmail(input);
            }

            service.updatePatient(patient);
            System.out.println("Patient updated successfully.");
        } else {
            System.out.println("Patient not found.");
        }

    }

    public static void deletePatient() {
        System.out.println("\n=== Delete Patient ===");
        long id = getIntInput("Enter patient ID: ");
        service.deletePatient(id);
        System.out.println("Patient deleted successfully.");
    }

    public static void listAllPatients() {
        System.out.println("\n=== All Patients ===");
        List<Patient> patients = service.getAllPatients();

        /**
         *
         * How the .forEach() method works:
         * The forEach() method is a default method in the Collection interface,
         * patients.forEach(the action allowing me to iterate over each element in the ArrayList)
         *
         * Patient: The name of the ArrayList to be iterated.
         * action: The operation that accepts an element in the ArrayList as its only argument
         *      and does not return any value.
         */

        patients.forEach(p -> System.out.println("ID: " + p.getId() +
                ", Name: " + p.getFirstName() + " " + p.getLastName() +
                ", Email: " + p.getEmail()));
    }


}
