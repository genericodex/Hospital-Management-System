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
     * <p>
     * The IDENTITY strategy relies on the database to generate new identifier values
     *</p>
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


    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Constructors

    /**
     * Default no-args constructor needed because Hibernate has to create instances of entities.
     * To accomplish this, Hibernate first uses the no-args constructor to instantiate the entity object,
     * then proceeds to populate the object’s properties with the corresponding data from the database.
     *
     */
    public Patient() {}

    public Patient(String firstName, String lastName, Date dateOfBirth, String contactNumber,
                   String address, String email, Boolean isDeleted, String medicalHistory) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
        this.address = address;
        this.email = email;
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null




        this.medicalHistory = medicalHistory;
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

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDeleted() {return isDeleted;}
    public void setDeleted(boolean deleted) {isDeleted = deleted;}
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

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

    /**
     *
     * The equals() method is overridden to provide a custom equality check
     * for Patient objects based on their ID.
     * It is annotated with @Override because it overrides the default equals() method
     * from the Object class.
     * <p>
     * @param o The object to compare with this Patient instance
     * <p>
     * The selected code overrides the equals and hashCode methods for the Patient entity in your Java project.
     * This ensures that two Patient objects are considered equal if they have the same non-null id, and their
     * hash codes are based on the id field. This is important for correct behavior in collections like
     * Set or as keys in a Map, and for JPA entity identity management.
     */

    /**
     *
     * This equals method says: “If the IDs are the same, these are the same patient.”
     * Then this hashCode method uses the ID, so two patients with the same ID get the same hash number.
     * In plain terms:
     * Imagine you have two ID cards for the same person. Even if the cards are printed at different times,
     * if the ID number is the same, it’s the same person. That’s what your code is doing for Patient objects.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient other = (Patient) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isValidPhoneNumber() {
        return contactNumber != null && contactNumber.matches("\\d{10}");
    }

    public boolean isValidBirthDate() {
        return dateOfBirth != null && !dateOfBirth.after(new java.util.Date());
    }
}