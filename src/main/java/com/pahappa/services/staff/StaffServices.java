//<<<<<<< Updated upstream
//package com.pahappa.services;
//
//import com.pahappa.constants.StaffRoles;
//import com.pahappa.models.Staff;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import static com.pahappa.util.StringUtil.*;
//import static com.pahappa.util.StringUtil.getIntInput;
//import static com.pahappa.util.StringUtil.getStringInput;
//
//public class StaffServices {
//
//    private static final HospitalService hospitalservice = new HospitalService();
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//    // Staff operations
//    public static void createStaff() {
//        System.out.println("\n=== Create New Staff ===");
//        String firstName, lastName, email, contact, department, password, staffStr;
//        Boolean isDeleted = false;
//        do {firstName = getStringInput("Enter first name: ");
//            if (firstName.isEmpty()) {System.out.println("First name cannot be empty. Please try again.");}
//        }while (firstName.trim().isEmpty());
//        do {lastName = getStringInput("Enter last name: ");
//            if (lastName.isEmpty()) {System.out.println("Last name cannot be empty. Please try again.");}
//        }while (lastName.trim().isEmpty());
//        do {email = getStringInput("Enter email: ");
//            if (email.isEmpty()) {System.out.println("Email cannot be empty. Please try again.");}
//        }while (email.trim().isEmpty());
//        do {contact = getStringInput("Enter phone number: ");
//            if (contact.isEmpty()) {System.out.println("Phone number cannot be empty. Please try again.");}
//        }while (contact.trim().isEmpty());
//
//        System.out.println("Available Staff roles:");
//        for (int i = 0; i < StaffRoles.values().length; i++) {
//            StaffRoles r= StaffRoles.values()[i];
//            System.out.println("- " + r);
//        }
//
//        do {staffStr = getStringInput("Enter Staff Roles: ");
//            if (staffStr.trim().isEmpty()) {System.out.println("role cannot be empty. Please try again.");
//            } else {try {StaffRoles.valueOf(staffStr.toUpperCase());
//            } catch (IllegalArgumentException e) {
//                System.out.println("Invalid role. Please enter a valid role from the list.");
//                staffStr = "";
//            }
//            }
//        }while (staffStr.trim().isEmpty());
//
//        StaffRoles role = StaffRoles.valueOf(staffStr.toUpperCase());
//
//
//        do {department = getStringInput("Enter department: ");
//            if (department.trim().isEmpty()) {System.out.println("Department cannot be empty. Please try again.");}
//        }while (department.trim().isEmpty());
//        // Hire date validation: must not be in the future
//        Date hireDate;
//        do {
//            hireDate = getDateInput("Enter hire date");
//            Date today = new Date();
//            if (hireDate.after(today)) {
//                System.out.println("Hire date cannot be in the future. Please enter a valid date.");
//                hireDate = null;
//            }
//        } while (hireDate == null);
//        do {password = getStringInput("Enter password: ");
//            if (password.isEmpty()) {System.out.println("Password cannot be empty. Please try again.");}
//        }while (password.trim().isEmpty());
//
//        Staff staff = hospitalservice.createStaff(firstName, lastName, email, contact, role,
//                department, hireDate, password, isDeleted);
//        System.out.println("Staff created successfully with ID: " + staff.getId());
//    }
//
//    public static void viewStaff() {
//        System.out.println("\n=== View Staff ===");
//        long id = getIntInput("Enter staff ID: ");
//        Staff staff = hospitalservice.getStaffById(id);
//        if (staff != null) {
//            System.out.println("Staff Details:");
//            System.out.println("Name: " + staff.getFirstName() + " " + staff.getLastName());
//            System.out.println("Email: " + staff.getEmail());
//            System.out.println("Phone: " + staff.getContactNumber());
//            System.out.println("Role: " + staff.getRole());
//            System.out.println("Department: " + staff.getDepartment());
//            System.out.println("Hire Date: " + dateFormat.format(staff.getHireDate()));
//        } else {
//            System.out.println("Staff not found.");
//        }
//    }
//
//    public static void updateStaff() {
//        System.out.println("\n=== Update Staff ===");
//        long id = getIntInput("Enter staff ID: ");
//        Staff staff = hospitalservice.getStaffById(id);
//        if (staff != null) {
//            String input;
//
//            input = getStringInput("Enter new first name (or press enter to skip): ");
//            if (!input.trim().isEmpty()) {
//                staff.setFirstName(input);
//            }
//
//            input = getStringInput("Enter new last name (or press enter to skip): ");
//            if (!input.trim().isEmpty()) {
//                staff.setLastName(input);
//            }
//
//            input = getStringInput("Enter new email (or press enter to skip): ");
//            if (!input.trim().isEmpty()) {
//                staff.setEmail(input);
//            }else if (!input.contains("@") || !input.contains(".")) {
//                System.out.println("Invalid email format. Please try again.");
//                input = "";
//            }
//
//            input = getStringInput("Enter new phone number (or press enter to skip): ");
//            if (!input.trim().isEmpty()) {
//                staff.setContactNumber(input);
//            }
//
//
//            input = getStringInput("Enter new department (or press enter to skip): ");
//            if (!input.trim().isEmpty()) {
//                staff.setDepartment(input);
//            }
//
//            hospitalservice.updateStaff(staff);
//            System.out.println("Staff updated successfully.");
//        } else {
//            System.out.println("Staff not found.");
//        }
//
//    }
//
//    public static void eraseStaff() {
//        System.out.println("\n=== Delete Staff ===");
//        long id = getIntInput("Enter staff ID: ");
//        hospitalservice.deleteStaff(id);
//        System.out.println("Staff deleted successfully.");
//    }
//
//    public static void deleteStaff() {
//        long id = getIntInput("Enter staff ID: ");
//        hospitalservice.softDeleteStaff(id);
//        System.out.println("Staff moved to bin (soft deleted).");
//    }
//
//    public static void restoreStaff() {
//        long id = getIntInput("Enter staff ID to restore: ");
//        hospitalservice.restoreStaff(id);
//        System.out.println("Staff restored from bin.");
//    }
//
//    public static void listAllStaff() {
//        List<Staff> staffList = hospitalservice.getAllActiveStaff();
//        if (staffList == null || staffList.isEmpty()) {
//            System.out.println("No staff members found.");
//            return;
//        }staffList.forEach(s -> System.out.println("ID: " + s.getId() +
//                ", Name: " + s.getFirstName() + " " + s.getLastName() +
//                ", Role: " + s.getRole() +
//                ", Department: " + s.getDepartment()));    }
//
//    public static void listDeletedStaff() {
//        List<Staff> deletedStaff = hospitalservice.getDeletedStaff();
//        // display deletedStaff
//
//    }
//
//    public static void listAllStaffs() {
//        System.out.println("\n=== All Staff ===");
//        List<Staff> staffList = hospitalservice.getAllStaff();
//
//    }
//
//
//}
//=======
////package com.pahappa.services;
////
////import com.pahappa.constants.StaffRoles;
////import com.pahappa.models.Staff;
////
////import java.text.SimpleDateFormat;
////import java.util.Date;
////import java.util.List;
////
////import static com.pahappa.util.StringUtil.*;
////import static com.pahappa.util.StringUtil.getIntInput;
////import static com.pahappa.util.StringUtil.getStringInput;
////
////public class StaffServices {
////
////    private static final HospitalService hospitalservice = new HospitalService();
////    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////
////    // Staff operations
////    public static void createStaff() {
////        System.out.println("\n=== Create New Staff ===");
////        String firstName, lastName, email, contact, department, password, staffStr;
////        Boolean isDeleted = false;
////        do {firstName = getStringInput("Enter first name: ");
////            if (firstName.isEmpty()) {System.out.println("First name cannot be empty. Please try again.");}
////        }while (firstName.trim().isEmpty());
////        do {lastName = getStringInput("Enter last name: ");
////            if (lastName.isEmpty()) {System.out.println("Last name cannot be empty. Please try again.");}
////        }while (lastName.trim().isEmpty());
////        do {email = getStringInput("Enter email: ");
////            if (email.isEmpty()) {System.out.println("Email cannot be empty. Please try again.");}
////        }while (email.trim().isEmpty());
////        do {contact = getStringInput("Enter phone number: ");
////            if (contact.isEmpty()) {System.out.println("Phone number cannot be empty. Please try again.");}
////        }while (contact.trim().isEmpty());
////
////        System.out.println("Available Staff roles:");
////        for (int i = 0; i < StaffRoles.values().length; i++) {
////            StaffRoles r= StaffRoles.values()[i];
////            System.out.println("- " + r);
////        }
////
////        do {staffStr = getStringInput("Enter Staff Roles: ");
////            if (staffStr.trim().isEmpty()) {System.out.println("role cannot be empty. Please try again.");
////            } else {try {StaffRoles.valueOf(staffStr.toUpperCase());
////            } catch (IllegalArgumentException e) {
////                System.out.println("Invalid role. Please enter a valid role from the list.");
////                staffStr = "";
////            }
////            }
////        }while (staffStr.trim().isEmpty());
////
////        StaffRoles role = StaffRoles.valueOf(staffStr.toUpperCase());
////
////
////        do {department = getStringInput("Enter department: ");
////            if (department.trim().isEmpty()) {System.out.println("Department cannot be empty. Please try again.");}
////        }while (department.trim().isEmpty());
////        // Hire date validation: must not be in the future
////        Date hireDate;
////        do {
////            hireDate = getDateInput("Enter hire date");
////            Date today = new Date();
////            if (hireDate.after(today)) {
////                System.out.println("Hire date cannot be in the future. Please enter a valid date.");
////                hireDate = null;
////            }
////        } while (hireDate == null);
////        do {password = getStringInput("Enter password: ");
////            if (password.isEmpty()) {System.out.println("Password cannot be empty. Please try again.");}
////        }while (password.trim().isEmpty());
////
////        Staff staff = hospitalservice.createStaff(firstName, lastName, email, contact, role,
////                department, hireDate, password, isDeleted);
////        System.out.println("Staff created successfully with ID: " + staff.getId());
////    }
////
////    public static void viewStaff() {
////        System.out.println("\n=== View Staff ===");
////        long id = getIntInput("Enter staff ID: ");
////        Staff staff = hospitalservice.getStaffById(id);
////        if (staff != null) {
////            System.out.println("Staff Details:");
////            System.out.println("Name: " + staff.getFirstName() + " " + staff.getLastName());
////            System.out.println("Email: " + staff.getEmail());
////            System.out.println("Phone: " + staff.getContactNumber());
////            System.out.println("Role: " + staff.getRole());
////            System.out.println("Department: " + staff.getDepartment());
////            System.out.println("Hire Date: " + dateFormat.format(staff.getHireDate()));
////        } else {
////            System.out.println("Staff not found.");
////        }
////    }
////
////    public static void updateStaff() {
////        System.out.println("\n=== Update Staff ===");
////        long id = getIntInput("Enter staff ID: ");
////        Staff staff = hospitalservice.getStaffById(id);
////        if (staff != null) {
////            String input;
////
////            input = getStringInput("Enter new first name (or press enter to skip): ");
////            if (!input.trim().isEmpty()) {
////                staff.setFirstName(input);
////            }
////
////            input = getStringInput("Enter new last name (or press enter to skip): ");
////            if (!input.trim().isEmpty()) {
////                staff.setLastName(input);
////            }
////
////            input = getStringInput("Enter new email (or press enter to skip): ");
////            if (!input.trim().isEmpty()) {
////                staff.setEmail(input);
////            }else if (!input.contains("@") || !input.contains(".")) {
////                System.out.println("Invalid email format. Please try again.");
////                input = "";
////            }
////
////            input = getStringInput("Enter new phone number (or press enter to skip): ");
////            if (!input.trim().isEmpty()) {
////                staff.setContactNumber(input);
////            }
////
////
////            input = getStringInput("Enter new department (or press enter to skip): ");
////            if (!input.trim().isEmpty()) {
////                staff.setDepartment(input);
////            }
////
////            hospitalservice.updateStaff(staff);
////            System.out.println("Staff updated successfully.");
////        } else {
////            System.out.println("Staff not found.");
////        }
////
////    }
////
////    public static void eraseStaff() {
////        System.out.println("\n=== Delete Staff ===");
////        long id = getIntInput("Enter staff ID: ");
////        hospitalservice.deleteStaff(id);
////        System.out.println("Staff deleted successfully.");
////    }
////
////    public static void deleteStaff() {
////        long id = getIntInput("Enter staff ID: ");
////        hospitalservice.softDeleteStaff(id);
////        System.out.println("Staff moved to bin (soft deleted).");
////    }
////
////    public static void restoreStaff() {
////        long id = getIntInput("Enter staff ID to restore: ");
////        hospitalservice.restoreStaff(id);
////        System.out.println("Staff restored from bin.");
////    }
////
////    public static void listAllStaff() {
////        List<Staff> staffList = hospitalservice.getAllActiveStaff();
////        if (staffList == null || staffList.isEmpty()) {
////            System.out.println("No staff members found.");
////            return;
////        }staffList.forEach(s -> System.out.println("ID: " + s.getId() +
////                ", Name: " + s.getFirstName() + " " + s.getLastName() +
////                ", Role: " + s.getRole() +
////                ", Department: " + s.getDepartment()));    }
////
////    public static void listDeletedStaff() {
////        List<Staff> deletedStaff = hospitalservice.getDeletedStaff();
////        // display deletedStaff
////
////    }
////
////    public static void listAllStaffs() {
////        System.out.println("\n=== All Staff ===");
////        List<Staff> staffList = hospitalservice.getAllStaff();
////
////    }
////
////
////}
//>>>>>>> Stashed changes
