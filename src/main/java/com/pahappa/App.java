//package com.pahappa;
//
//import com.pahappa.dao.StaffDao;
//import com.pahappa.util.HibernateUtil;
//import java.text.ParseException;
//import java.util.Scanner;
//import static com.pahappa.services.AppointmentServices.*;
//import static com.pahappa.services.StaffServices.*;
//import com.pahappa.models.Staff;
//import static com.pahappa.services.BillingServices.*;
//import static com.pahappa.services.DoctorServices.*;
//import static com.pahappa.services.PatientServices.*;
//import static com.pahappa.util.StringUtil.*;
//
//
//public class App {
//    private static final Scanner scanner = new Scanner(System.in);
//
//    public static void main(String[] args) {
//        try {
//            HibernateUtil.getSessionFactory().openSession();
//            System.out.println("Database connection successful!");
//
//            StaffDao staffDao = new StaffDao();
//            Staff loggedInStaff = null;
//            while (loggedInStaff == null) {
//                System.out.println("=== Staff Login ===");
//                String email = getStringInput("Enter email: ");
//                String password = getStringInput("Enter password: ");
//                loggedInStaff = staffDao.authenticate(email, password);
//                if (loggedInStaff == null) {
//                    System.out.println("Invalid email or password. Please try again.");
//                }
//            }
//
//            System.out.println("Login successful!! Welcome, " + loggedInStaff.getFirstName() + " " + loggedInStaff.getLastName() + "!");
//
//
//            boolean running = true;
//            while (running) {
//                displayMainMenu();
//                int choice = getIntInput("Enter your choice: ");
//
//                switch (choice) {
//                    case 1: {handlePatientOperations();break;
//                    }case 2 :{handleDoctorOperations();break;
//                    }case 3 :{handleAppointmentOperations();break;
//                    }case 4 :{handleBillingOperations();break;
//                    }case 5 :{handleStaffOperations();break;
//                    }case 0 :{running = false;break;
//                    }default :{System.out.println("Invalid choice. Please try again.");break;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Database connection failed!");
//            e.printStackTrace();
//        } finally {
//            HibernateUtil.shutdown();
//            scanner.close();
//        }
//    }
//    private static void displayMainMenu() {
//        System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
//        System.out.println("1. Patient Operations");
//        System.out.println("2. Doctor Operations");
//        System.out.println("3. Appointment Operations");
//        System.out.println("4. Billing Operations");
//        System.out.println("5. Staff Operations");
//        System.out.println("0. Exit");
//    }
//
//    private static void handlePatientOperations() {
//        while (true) {
//            System.out.println("\n===== PATIENT OPERATIONS =====");
//            System.out.println("1. Create Patient");
//            System.out.println("2. View Patient");
//            System.out.println("3. Update Patient");
//            System.out.println("4. Delete Patient");
//            System.out.println("5. List All Patients");
//            System.out.println("0. Back to Main Menu");
//
//            int choice = getIntInput("Enter your choice: ");
//
//            switch (choice) {
//                case 1 :{createPatient();break;
//                }case 2 :{viewPatient();break;
//                }case 3 :{updatePatient();break;
//                }case 4 :{deletePatient();break;
//                }case 5 :{listAllPatients();break;
//                }case 0 :{return; // Go back to main menu
//                }default :{System.out.println("Invalid choice. Please try again.");break;
//                }
//            }
//        }
//    }
//
//    private static void handleDoctorOperations() {
//        while (true) {
//            System.out.println("\n===== DOCTOR OPERATIONS =====");
//            System.out.println("1. Create Doctor");
//            System.out.println("2. View Doctor");
//            System.out.println("3. Update Doctor");
//            System.out.println("4. Delete Doctor");
//            System.out.println("5. List All Doctors");
//            System.out.println("0. Back to Main Menu");
//
//            int choice = getIntInput("Enter your choice: ");
//
//            switch (choice) {
//                case 1 :{createDoctor();break;
//                }case 2 :{viewDoctor();break;
//                }case 3 :{updateDoctor();break;
//                }case 4 :{deleteDoctor();break;
//                }case 5 :{listAllDoctors();break;
//                }case 0 :{ return;
//                }default :{System.out.println("Invalid choice. Please try again.");break;
//                }
//            }
//        }
//    }
//
//    private static void handleAppointmentOperations() throws ParseException {
//        while (true) {
//            System.out.println("\n===== APPOINTMENT OPERATIONS =====");
//            System.out.println("1. Create Appointment");
//            System.out.println("2. View Appointment");
//            System.out.println("3. Update Appointment");
//            System.out.println("4. Delete Appointment");
//            System.out.println("5. List All Appointments");
//            System.out.println("0. Back to Main Menu");
//
//            int choice = getIntInput("Enter your choice: ");
//
//            switch (choice) {
//                case 1 :{createAppointment();break;
//                }case 2 :{viewAppointment();break;
//                }case 3 :{updateAppointment();break;
//                }case 4 :{deleteAppointment();break;
//                }case 5 :{listAllAppointments();break;
//                }case 6: {listAppointmentsByDoctor(); break;
//                }case 7: {listAppointmentsByPatient();break;
//                }case 0 :{ return;
//                }default :{System.out.println("Invalid choice. Please try again.");break;
//                }
//            }
//        }
//    }
//
//    private static void handleBillingOperations() {
//        while (true) {
//            System.out.println("\n===== BILLING OPERATIONS =====");
//            System.out.println("1. Create Bill");
//            System.out.println("2. View Bill");
//            System.out.println("3. Process Payment");
//            System.out.println("4. List All Bills");
//            System.out.println("0. Back to Main Menu");
//
//            int choice = getIntInput("Enter your choice: ");
//
//            switch (choice) {
//                case 1 :{createBill();break;
//                }case 2 :{viewBill();break;
//                }case 3 :{processPayment();break;
//                }case 4 :{listAllBills();break;
//                }case 0 :{ return;
//                }default :{System.out.println("Invalid choice. Please try again.");break;
//                }
//            }
//        }
//    }
//
//    private static void handleStaffOperations() {
//        while (true) {
//            System.out.println("\n===== STAFF OPERATIONS =====");
//            System.out.println("1. Create Staff");
//            System.out.println("2. View Staff");
//            System.out.println("3. Update Staff");
//            System.out.println("4. Delete Staff");
//            System.out.println("5. List All Staff");
//            System.out.println("0. Back to Main Menu");
//
//            int choice = getIntInput("Enter your choice: ");
//
//            switch (choice) {
//                case 1 :{createStaff();break;
//                }case 2 :{viewStaff();break;
//                }case 3 :{updateStaff();break;
//                }case 4 :{deleteStaff();break;
//                }case 5 :{listAllStaff();break;
//                }case 0 :{ return;
//                }default :{System.out.println("Invalid choice. Please try again.");break;
//                }
//            }
//        }
//    }
//}
//=======
////package com.pahappa;
////
////import com.pahappa.dao.StaffDao;
////import com.pahappa.util.HibernateUtil;
////import java.text.ParseException;
////import java.util.Scanner;
////import static com.pahappa.services.AppointmentServices.*;
////import static com.pahappa.services.StaffServices.*;
////import com.pahappa.models.Staff;
////import static com.pahappa.services.BillingServices.*;
////import static com.pahappa.services.DoctorServices.*;
////import static com.pahappa.services.PatientServices.*;
////import static com.pahappa.util.StringUtil.*;
////
////
////public class App {
////    private static final Scanner scanner = new Scanner(System.in);
////
////    public static void main(String[] args) {
////        try {
////            HibernateUtil.getSessionFactory().openSession();
////            System.out.println("Database connection successful!");
////
////            StaffDao staffDao = new StaffDao();
////            Staff loggedInStaff = null;
////            while (loggedInStaff == null) {
////                System.out.println("=== Staff Login ===");
////                String email = getStringInput("Enter email: ");
////                String password = getStringInput("Enter password: ");
////                loggedInStaff = staffDao.authenticate(email, password);
////                if (loggedInStaff == null) {
////                    System.out.println("Invalid email or password. Please try again.");
////                }
////            }
////
////            System.out.println("Login successful!! Welcome, " + loggedInStaff.getFirstName() + " " + loggedInStaff.getLastName() + "!");
////
////
////            boolean running = true;
////            while (running) {
////                displayMainMenu();
////                int choice = getIntInput("Enter your choice: ");
////
////                switch (choice) {
////                    case 1: {handlePatientOperations();break;
////                    }case 2 :{handleDoctorOperations();break;
////                    }case 3 :{handleAppointmentOperations();break;
////                    }case 4 :{handleBillingOperations();break;
////                    }case 5 :{handleStaffOperations();break;
////                    }case 0 :{running = false;break;
////                    }default :{System.out.println("Invalid choice. Please try again.");break;
////                    }
////                }
////            }
////        } catch (Exception e) {
////            System.out.println("Database connection failed!");
////            e.printStackTrace();
////        } finally {
////            HibernateUtil.shutdown();
////            scanner.close();
////        }
////    }
////    private static void displayMainMenu() {
////        System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
////        System.out.println("1. Patient Operations");
////        System.out.println("2. Doctor Operations");
////        System.out.println("3. Appointment Operations");
////        System.out.println("4. Billing Operations");
////        System.out.println("5. Staff Operations");
////        System.out.println("0. Exit");
////    }
////
////    private static void handlePatientOperations() {
////        while (true) {
////            System.out.println("\n===== PATIENT OPERATIONS =====");
////            System.out.println("1. Create Patient");
////            System.out.println("2. View Patient");
////            System.out.println("3. Update Patient");
////            System.out.println("4. Delete Patient");
////            System.out.println("5. List All Patients");
////            System.out.println("0. Back to Main Menu");
////
////            int choice = getIntInput("Enter your choice: ");
////
////            switch (choice) {
////                case 1 :{createPatient();break;
////                }case 2 :{viewPatient();break;
////                }case 3 :{updatePatient();break;
////                }case 4 :{deletePatient();break;
////                }case 5 :{listAllPatients();break;
////                }case 0 :{return; // Go back to main menu
////                }default :{System.out.println("Invalid choice. Please try again.");break;
////                }
////            }
////        }
////    }
////
////    private static void handleDoctorOperations() {
////        while (true) {
////            System.out.println("\n===== DOCTOR OPERATIONS =====");
////            System.out.println("1. Create Doctor");
////            System.out.println("2. View Doctor");
////            System.out.println("3. Update Doctor");
////            System.out.println("4. Delete Doctor");
////            System.out.println("5. List All Doctors");
////            System.out.println("0. Back to Main Menu");
////
////            int choice = getIntInput("Enter your choice: ");
////
////            switch (choice) {
////                case 1 :{createDoctor();break;
////                }case 2 :{viewDoctor();break;
////                }case 3 :{updateDoctor();break;
////                }case 4 :{deleteDoctor();break;
////                }case 5 :{listAllDoctors();break;
////                }case 0 :{ return;
////                }default :{System.out.println("Invalid choice. Please try again.");break;
////                }
////            }
////        }
////    }
////
////    private static void handleAppointmentOperations() throws ParseException {
////        while (true) {
////            System.out.println("\n===== APPOINTMENT OPERATIONS =====");
////            System.out.println("1. Create Appointment");
////            System.out.println("2. View Appointment");
////            System.out.println("3. Update Appointment");
////            System.out.println("4. Delete Appointment");
////            System.out.println("5. List All Appointments");
////            System.out.println("0. Back to Main Menu");
////
////            int choice = getIntInput("Enter your choice: ");
////
////            switch (choice) {
////                case 1 :{createAppointment();break;
////                }case 2 :{viewAppointment();break;
////                }case 3 :{updateAppointment();break;
////                }case 4 :{deleteAppointment();break;
////                }case 5 :{listAllAppointments();break;
////                }case 6: {listAppointmentsByDoctor(); break;
////                }case 7: {listAppointmentsByPatient();break;
////                }case 0 :{ return;
////                }default :{System.out.println("Invalid choice. Please try again.");break;
////                }
////            }
////        }
////    }
////
////    private static void handleBillingOperations() {
////        while (true) {
////            System.out.println("\n===== BILLING OPERATIONS =====");
////            System.out.println("1. Create Bill");
////            System.out.println("2. View Bill");
////            System.out.println("3. Process Payment");
////            System.out.println("4. List All Bills");
////            System.out.println("0. Back to Main Menu");
////
////            int choice = getIntInput("Enter your choice: ");
////
////            switch (choice) {
////                case 1 :{createBill();break;
////                }case 2 :{viewBill();break;
////                }case 3 :{processPayment();break;
////                }case 4 :{listAllBills();break;
////                }case 0 :{ return;
////                }default :{System.out.println("Invalid choice. Please try again.");break;
////                }
////            }
////        }
////    }
////
////    private static void handleStaffOperations() {
////        while (true) {
////            System.out.println("\n===== STAFF OPERATIONS =====");
////            System.out.println("1. Create Staff");
////            System.out.println("2. View Staff");
////            System.out.println("3. Update Staff");
////            System.out.println("4. Delete Staff");
////            System.out.println("5. List All Staff");
////            System.out.println("0. Back to Main Menu");
////
////            int choice = getIntInput("Enter your choice: ");
////
////            switch (choice) {
////                case 1 :{createStaff();break;
////                }case 2 :{viewStaff();break;
////                }case 3 :{updateStaff();break;
////                }case 4 :{deleteStaff();break;
////                }case 5 :{listAllStaff();break;
////                }case 0 :{ return;
////                }default :{System.out.println("Invalid choice. Please try again.");break;
////                }
////            }
////        }
////    }
////}
