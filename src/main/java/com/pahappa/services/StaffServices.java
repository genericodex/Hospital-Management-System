package com.pahappa.services;

import com.pahappa.constants.StaffRoles;
import com.pahappa.models.Staff;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.pahappa.util.StringUtil.*;
import static com.pahappa.util.StringUtil.getIntInput;
import static com.pahappa.util.StringUtil.getStringInput;

public class StaffServices {

    private static final HospitalService service = new HospitalService();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Staff operations
    public static void createStaff() {
        System.out.println("\n=== Create New Staff ===");
        String firstName, lastName, email, contact, department, password, staffStr;
        do {firstName = getStringInput("Enter first name: ");
            if (firstName.isEmpty()) {System.out.println("First name cannot be empty. Please try again.");}
        }while (firstName.trim().isEmpty());
        do {lastName = getStringInput("Enter last name: ");
            if (lastName.isEmpty()) {System.out.println("Last name cannot be empty. Please try again.");}
        }while (lastName.trim().isEmpty());
        do {email = getStringInput("Enter email: ");
            if (email.isEmpty()) {System.out.println("Email cannot be empty. Please try again.");}
        }while (email.trim().isEmpty());
        do {contact = getStringInput("Enter phone number: ");
            if (contact.isEmpty()) {System.out.println("Phone number cannot be empty. Please try again.");}
        }while (contact.trim().isEmpty());
//        do {role = getStringInput("Enter role (e.g., RECEPTIONIST, NURSE): ");
//            if (role.isEmpty()) {System.out.println("Role cannot be empty. Please try again.");}
//        }while (role.trim().isEmpty());

        System.out.println("Available Staff roles:");
        for (int i = 0; i < StaffRoles.values().length; i++) {
            StaffRoles r= StaffRoles.values()[i];
            System.out.println("- " + r);
        }

        do {staffStr = getStringInput("Enter Staff Roles: ");
            if (staffStr.isEmpty()) {System.out.println("Specialization cannot be empty. Please try again.");
            } else {try {StaffRoles.valueOf(staffStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid specialization. Please enter a valid specialization from the list.");
                staffStr = "";
            }
            }
        }while (staffStr.trim().isEmpty());

        StaffRoles role = StaffRoles.valueOf(staffStr.toUpperCase());


        do {department = getStringInput("Enter department: ");
            if (department.isEmpty()) {System.out.println("Department cannot be empty. Please try again.");}
        }while (department.trim().isEmpty());
        Date hireDate = getDateInput("Enter hire date");
        do {password = getStringInput("Enter password: ");
            if (password.isEmpty()) {System.out.println("Password cannot be empty. Please try again.");}
        }while (password.trim().isEmpty());

        Staff staff = service.createStaff(firstName, lastName, email, contact, role,
                department, hireDate, password);
        System.out.println("Staff created successfully with ID: " + staff.getId());
    }

    public static void viewStaff() {
        System.out.println("\n=== View Staff ===");
        long id = getIntInput("Enter staff ID: ");
        Staff staff = service.getStaffById(id);
        if (staff != null) {
            System.out.println("Staff Details:");
            System.out.println("Name: " + staff.getFirstName() + " " + staff.getLastName());
            System.out.println("Email: " + staff.getEmail());
            System.out.println("Phone: " + staff.getContactNumber());
            System.out.println("Role: " + staff.getRole());
            System.out.println("Department: " + staff.getDepartment());
            System.out.println("Hire Date: " + dateFormat.format(staff.getHireDate()));
        } else {
            System.out.println("Staff not found.");
        }
    }

    public static void updateStaff() {
        System.out.println("\n=== Update Staff ===");
        long id = getIntInput("Enter staff ID: ");
        Staff staff = service.getStaffById(id);
        if (staff != null) {
            String input;

            input = getStringInput("Enter new first name (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                staff.setFirstName(input);
            }

            input = getStringInput("Enter new last name (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                staff.setLastName(input);
            }

            input = getStringInput("Enter new email (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                staff.setEmail(input);
            }

            input = getStringInput("Enter new phone number (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                staff.setContactNumber(input);
            }


            input = getStringInput("Enter new department (or press enter to skip): ");
            if (!input.trim().isEmpty()) {
                staff.setDepartment(input);
            }

            service.updateStaff(staff);
            System.out.println("Staff updated successfully.");
        } else {
            System.out.println("Staff not found.");
        }

    }

    public static void deleteStaff() {
        System.out.println("\n=== Delete Staff ===");
        long id = getIntInput("Enter staff ID: ");
        service.deleteStaff(id);
        System.out.println("Staff deleted successfully.");
    }

    public static void listAllStaff() {
        System.out.println("\n=== All Staff ===");
        List<Staff> staffList = service.getAllStaff();
        if (staffList == null || staffList.isEmpty()) {
            System.out.println("No staff members found.");
            return;
        }staffList.forEach(s -> System.out.println("ID: " + s.getId() +
                ", Name: " + s.getFirstName() + " " + s.getLastName() +
                ", Role: " + s.getRole() +
                ", Department: " + s.getDepartment()));
    }


}
