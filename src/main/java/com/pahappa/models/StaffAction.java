package com.pahappa.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "staff_actions")
public class StaffAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Column(nullable = false)
    private String actionType; // e.g., CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String entityType; // e.g., Patient, Doctor

    @Column(nullable = false)
    private Long entityId;

    @Column(length = 1000)
    private String details;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}

