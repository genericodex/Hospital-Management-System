package com.pahappa.services;

import com.pahappa.models.Billing;
import com.pahappa.models.Patient;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.pahappa.util.StringUtil.*;

public class BillingServices {
    private static final HospitalService service = new HospitalService();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Billing operations
    public static void createBill() {
        System.out.println("\n=== Create New Bill ===");
        long patientId = getIntInput("Enter patient ID: ");
        Patient patient = service.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        double amount = getDoubleInput("Enter bill amount: ");
        String description = getStringInput("Enter service description: ");

        Billing billing = service.createBilling(patient, amount, description);
        System.out.println("Bill created successfully with ID: " + billing.getId());
    }

    public static void viewBill() {
        System.out.println("\n=== View Bill ===");
        long id = getIntInput("Enter bill ID: ");
        Billing billing = service.getBillingById(id);
        if (billing != null) {
            System.out.println("Bill Details:");
            System.out.println("Patient: " + billing.getPatient().getFirstName() + " " + billing.getPatient().getLastName());
            System.out.println("Amount: $" + billing.getAmount());
            System.out.println("Description: " + billing.getServiceDescription());
            System.out.println("Status: " + billing.getStatus());
            System.out.println("Date: " + dateFormat.format(billing.getBillDate()));
        } else {
            System.out.println("Bill not found.");
        }
    }

    public static void processPayment() {
        System.out.println("\n=== Process Payment ===");
        long id = getIntInput("Enter bill ID: ");
        Billing billing = service.getBillingById(id);
        if (billing != null) {
            System.out.println("Payment methods: CASH, CREDIT_CARD, DEBIT_CARD, INSURANCE");
            String paymentMethod = getStringInput("Enter payment method: ");
            service.processPayment(billing, paymentMethod);
            System.out.println("Payment processed successfully.");
        } else {
            System.out.println("Bill not found.");
        }
    }

    public static void listAllBills() {
        System.out.println("\n=== All Bills ===");
        long patientId = getIntInput("Enter patient ID (or 0 for all bills): ");
        List<Billing> bills;
        if (patientId > 0) {
            bills = service.getBillingsByPatient(patientId);
        } else {
            bills = service.getAllBillings();
        }

        if (bills == null || bills.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }
        bills.forEach(b -> System.out.println("ID: " + b.getId() +
                ", Patient: " + b.getPatient().getFirstName() + " " + b.getPatient().getLastName() +
                ", Amount: $" + b.getAmount() +
                ", Status: " + b.getStatus()));
    }



}
