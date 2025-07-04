package com.pahappa.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "actionType", length = 32, nullable = false)
    private String actionType; // CREATE, UPDATE, DELETE, etc.

    @Column(name = "entityName", length = 64, nullable = false)
    private String entityName;

    @Column(name = "entityId", length = 64)
    private String entityId;

    @Column(name = "oldValue")
    private String oldValue;

    @Column(name = "newValue")
    private String newValue;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "staffId")
    private Long staffId;

    @Column(name = "staffName", length = 128)
    private String staffName;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getDiff() {
        if (oldValue == null && newValue == null) return "No changes";
        if (oldValue == null) return "Added: " + newValue;
        if (newValue == null) return "Removed: " + oldValue;
        StringBuilder diff = new StringBuilder();
        String[] oldLines = oldValue.split("\\r?\\n");
        String[] newLines = newValue.split("\\r?\\n");
        int max = Math.max(oldLines.length, newLines.length);
        for (int i = 0; i < max; i++) {
            String oldLine = i < oldLines.length ? oldLines[i] : "";
            String newLine = i < newLines.length ? newLines[i] : "";
            if (!oldLine.equals(newLine)) {
                if (!oldLine.isEmpty()) diff.append("- ").append(oldLine).append("\n");
                if (!newLine.isEmpty()) diff.append("+ ").append(newLine).append("\n");
            }
        }
        if (diff.isEmpty()) return "No changes";
        return diff.toString();
    }
}
