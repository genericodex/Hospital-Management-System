package com.pahappa.models;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String role; // RECEPTIONIST, NURSE, PHARMACIST, ADMIN

    @Column(nullable = false)
    private String department;

    @Column(name = "hire_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date hireDate;

    @Column(nullable = false)
    private String password;

    // Constructors
    public Staff() {}

    public Staff(String firstName, String lastName, String email, String contactNumber,
                 String role, String department, Date hireDate, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.role = role;
        this.department = department;
        this.hireDate = hireDate;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}