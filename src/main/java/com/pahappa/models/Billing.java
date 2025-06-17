package com.pahappa.models;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "billings")
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "bill_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date billDate;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status; // PENDING, PAID, PARTIAL

    @Column(name = "payment_method")
    private String paymentMethod; // CASH, CARD, INSURANCE

    @Column(name = "insurance_claim_id")
    private String insuranceClaimId;

    @Column(name = "service_description")
    private String serviceDescription;

    @Column(name = "tax_amount")
    private Double taxAmount;

    @Column(name = "discount_amount")
    private Double discountAmount;

    // Constructors
    public Billing() {}

    public Billing(Patient patient, Date billDate, Double amount, String status) {
        this.patient = patient;
        this.billDate = billDate;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getInsuranceClaimId() {
        return insuranceClaimId;
    }

    public void setInsuranceClaimId(String insuranceClaimId) {
        this.insuranceClaimId = insuranceClaimId;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    // Helper method to calculate total amount
    public Double getTotalAmount() {
        double total = amount;
        if (taxAmount != null) total += taxAmount;
        if (discountAmount != null) total -= discountAmount;
        return total;
    }

    @Override
    public String toString() {
        return "Billing{" +
                "id=" + id +
                ", patient=" + patient.getFirstName() + " " + patient.getLastName() +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}