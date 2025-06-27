package com.pahappa.models;
import jakarta.persistence.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


/**
 * @Entity declares the class as an entity (i.e. a persistent POJO class),
 *          marking this class as a JPA entity, making it persistable to the database.
 * @Id declares the identifier property of this entity *
 * @Table element contains a schema and catalog attributes
 *
 *
 */

@Entity
@Table(name = "patients")
public class Patient {
    /**
     * Unique identifier for the patient.
     * @Id marks this as the primary key
     * @GeneratedValue specifies auto-increment strategy
     * The IDENTITY strategy relies on the database to generate new identifier values
     *
     * Using Long for entity IDs allows for null values before
     * persistence, which is necessary for Hibernate/JPA to
     * manage entity lifecycle and primary key generation properly.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "contact_number", nullable = false, unique = true)
    private String contactNumber;

    @Column(nullable = false)
    private String address;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "insurance_number")
    private String insuranceNumber;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Constructors

    /**
     * Default no-args constructor needed because Hibernate has to create instances of entities.
     * To accomplish this, Hibernate first uses the no-args constructor to instantiate the entity object,
     * then proceeds to populate the object’s properties with the corresponding data from the database.
     *
     */
    public Patient() {}

    public Patient(String firstName, String lastName, Date dateOfBirth, String contactNumber,
                   String address, String email, Boolean isDeleted) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
        this.address = address;
        this.email = email;
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getMedicalHistory() {
//        return medicalHistory;
//    }
//
//    public void setMedicalHistory(String medicalHistory) {
//        this.medicalHistory = medicalHistory;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDeleted() {return isDeleted;}

    public void setDeleted(boolean deleted) {isDeleted = deleted;}

    /**
     *  The toString() method, which provides a string representation of the Patient object.
     *  It is annotated with @Override because it overrides
     *  the default toString() method from the Object class.
     * @return
     */
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return id != null && id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}