package com.pahappa.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "permissions")
public class Permissions implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String permissionKey; // e.g., "PATIENT_DELETE", "APPOINTMENT_CREATE"

    @Column(nullable = false)
    private String description; // e.g., "Allows user to delete patient records"

    // Constructors, Getters, Setters

    public Permissions() {}

    // equals() and hashCode() are crucial for collection management
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permissions that = (Permissions) o;
        return Objects.equals(permissionKey, that.permissionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionKey);
    }


    @Override
    public String toString() {
        return permissionKey; // Important for converters and debugging
    }

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPermissionKey() { return permissionKey; }
    public void setPermissionKey(String permissionKey) { this.permissionKey = permissionKey; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


}