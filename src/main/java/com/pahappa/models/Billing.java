package com.pahappa.models;
import jakarta.persistence.*;
import java.util.Date;
import com.pahappa.constants.BillingStatus;

@Entity
@Table(name = "billings")
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * When I use FetchType.LAZY, the related Patient data is not loaded immediately
      with each Billing. Instead, it is only loaded when you try to access the patient property.
      If your database session is already closed by that time,
      Hibernate cannot fetch the Patient data, giving me a LazyInitializationException,
      and then I lose DB connection.
     * But FetchType.EAGER allows the patient data is loaded together with each Billing
     * So LAZY loading needs an open database session when you access related data;
     * otherwise, you get errors.
     */

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "bill_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date billDate;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingStatus status;


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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // Constructors
    public Billing() {}

    public Billing(Patient patient, Date billDate, Double amount, BillingStatus status, Boolean isDeleted) {
        this.patient = patient;
        this.billDate = billDate;
        this.amount = amount;
        this.status = status;
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null
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

    public BillingStatus getStatus() {
        return status;
    }

    public void setStatus(BillingStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

//    public String getInsuranceClaimId() {
//        return insuranceClaimId;
//    }
//
//    public void setInsuranceClaimId(String insuranceClaimId) {
//        this.insuranceClaimId = insuranceClaimId;
//    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
//
//    public Double getTaxAmount() {
//        return taxAmount;
//    }
//
//    public void setTaxAmount(Double taxAmount) {
//        this.taxAmount = taxAmount;
//    }
//
//    public Double getDiscountAmount() {
//        return discountAmount;
//    }
//
//    public void setDiscountAmount(Double discountAmount) {
//        this.discountAmount = discountAmount;
//    }
//
//    // Helper method to calculate the total amount
//    public Double getTotalAmount() {
//        double total = amount;
//        if (taxAmount != null) total += taxAmount;
//        if (discountAmount != null) total -= discountAmount;
//        return total;
//    }

    /**
     * I am using the toString method to create the string representation of an object.
     * It is commonly overridden to return a human-readable view of the object's state, especially when debugging and logging.
     * @return
     */
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