package com.pahappa.models;
import jakarta.persistence.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


/**
 *   @Entity declares the class as an entity (i.e. a persistent POJO class),
 *  it Marks this Java class as something that can be saved to a database.
 *  Hibernate now knows this is a special class.
 *
 *  @Table element contains a schema and catalog attributes. It Tells Hibernate that this class
 *  corresponds to the table named "patients" in your database.
 */
@Entity
@Table(name = "patients")
public class Patient {
    /**
     * Unique identifier for the patient.
     * @Id marks this as the primary key. This is the unique identifier for each patient,
     * like a NIN. No two patients can have the same ID.
     * @GeneratedValue specifies auto-increment strategy
     * <p><p>
     * @GenerationType.IDENTITY strategy relies on the database to generate new identifier values.
     * This means we are letting the database handle it
      by auto-incrementing the number for each new patient.
     *</p>
     * @Long Using Long for entity IDs allows for null values before
     * persistence, which is necessary for Hibernate/JPA to
     * manage entity lifecycle and primary key generation properly.
     *
     * @class keyword is the most fundamental part of Java. It tells the compiler, "I am defining a new blueprint."
     */


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * @Column maps this Java field to a column in the 'patients' table.
     *     name = "first_name": The column in the DB is called 'first_name'.
     *     nullable = false: This field cannot be empty; it's a required field.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * @Temporal tells Hibernate how to handle old java.util.Date types.
     *     TemporalType.DATE means we only care about the date (e.g., 2025-07-16) and not the time.
     *     TemporalType.TIMESTAMP means we care about the date and time (e.g., 2025-07-16 15:30:00)
     */
    @Column(name = "date_of_birth", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    /**
     * @private is the opposite of public.
     * It's like putting a "MEMBERS ONLY" sign on the door.
     * It means these variables can only be accessed
     * or changed by code inside the Patient class itself.
     * <p>
     * By using this, am promoting encapsulation,
     * by forcing other code to go through controlled "gateways"
     * —the public getters and setters—to interact with the data.
     */
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
    /**
     * A "soft delete" flag. Instead of actually deleting a row from the database,
     *     we just set this to 'true'. This is great for keeping records, and restoration.
     */
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


    /**
     * Default no-args constructor needed because Hibernate has to create instances of entities.
     * To accomplish this, Hibernate first uses the no-args constructor to instantiate the entity object,
     * then proceeds to populate the object’s properties with the corresponding data from the database.
     * <p>
     *  The "no-args constructor". Hibernate requires a blank, empty constructor
     *      so it can create an empty Patient object first, before filling it with
     *     data from the database.
     * <p>
     * By making the Patient class public, you are allowing any other class in your entire project
     * (like your DAOs, Services, and JSF Beans) to see it, create new Patient objects, and use them.
     * This is essential for the class to be useful across your application.
     */
    public Patient() {}

    /**
     *Inside the constructor Patient(String firstName, ...), I have two variables named firstName:
     * the parameter passed into the method, and the class's field.
     * @this keyword is used to remove ambiguity.
     * <p>
     * •this.firstName means "the firstName field that belongs to this specific object that is being created."
     * firstName (without this) means "the firstName parameter that was just passed into this method."
     */
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

    /**
     * @Getters-Setters are standard Java methods that allow other parts
     *     of your code to access and change the private fields of this object.
     *     JSF and Hibernate both rely heavily on these public methods.
     * <p>
     *  They are the public-facing API for your private data.
     *  JSF heavily relies on them. When I write #{patientBean.patient.firstName}
     *  in my XHTML page, JSF is actually calling the getFirstName() method behind the scenes.
     *  When you submit a form, JSF calls the setFirstName() method.
     * <p>
     * @Long is a wrapper class for a primitive long. It holds a whole number.
     * I used the Long (capital L) instead of long (lowercase l) because
     * Long can hold a special value: null. This is critical for Hibernate.
     * When you create a new patient object that hasn't been saved to the database yet, so its id is null.
     * A primitive long cannot be null.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     *  @Getter method that returns the value of a private field.
     *  It doesn't take any parameters.
     *  Its name always starts with get (or is for booleans).
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *  @Setter method that sets or changes the value of a private field.
     *  It takes one parameter (the new value) and usually returns void (nothing).
     *  Its name always starts with set.
     * <p>
     *      //// I perform an action and return a String.
     * <p>
     *     public String doSomethingAndReturnAString(String input) {
     * <p>
     *         //// ... do something with the input ...
     * <p>
     *         return "some string result";
     * <p>
     *     }
     *
     */
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
     *  It is annotated with
     *  @Override because it overrides
     *  the default toString() method from the Object class.
     * <p>
     *  Without it, If I create a Patient object for "Eugene Tuhaise"
     *  and print it to the console without overriding toString(),
     *  I'll get something cryptic and unhelpful like this:
     *  <p>
     *  com.pahappa.models.Patient@1f32e575
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
     * <p>
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

    /**
     * @hashCode The partner to equals(). If two objects are 'equal', they MUST have the
     *      same hashCode. This rule ensures they work correctly when am using HashMaps and HashSets.
     */
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