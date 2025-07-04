package com.pahappa.models;
import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String specialization;


    @Column(name = "contact_number", nullable = false, unique = true)
    private String contactNumber;

    @Column(nullable = false, unique = true)
    private String email;

<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
    @Column(nullable = false)
    private String password;

>>>>>>> Stashed changes
>>>>>>> Stashed changes
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;


<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes

    // Constructors
    public Doctor() {}

<<<<<<< Updated upstream
    public Doctor(String firstName, String lastName, Specialization specialization,
<<<<<<< Updated upstream
                  String contactNumber, String email, Boolean isDeleted) {
=======
<<<<<<< Updated upstream
                  String contactNumber, String email) {
=======
<<<<<<< Updated upstream
                  String contactNumber, String email, Boolean isDeleted) {
=======
                  String contactNumber, String email) {
=======
    public Doctor(String firstName, String lastName, String specialization,
                  String contactNumber, String email, Boolean isDeleted, String password) {
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
<<<<<<< Updated upstream
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null
=======
<<<<<<< Updated upstream
=======
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null
        this.password = password;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    public boolean isDeleted() {
        return isDeleted;
    }
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return id != null && id.equals(doctor.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}