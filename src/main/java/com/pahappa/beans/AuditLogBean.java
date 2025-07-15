package com.pahappa.beans;

import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named
@ViewScoped
public class AuditLogBean implements Serializable {
    private List<AuditLog> auditLogs;
    private String selectedEntity;
    private String selectedActionType;
    private String selectedStaffName;
    private java.time.LocalDateTime startDate;
    private java.time.LocalDateTime endDate;
    @Inject // Use CDI to inject the DAO
    private transient AuditLogDao auditLogDao;

    @PostConstruct
    public void init() {
        filterAuditLogs(); // Load initial data, potentially with pre-set filters
    }

    public void filterAuditLogs() {
        auditLogs = auditLogDao.getFilteredAuditLogs(selectedEntity, selectedActionType, selectedStaffName, startDate, endDate);
    }

    public void clearFilters() {
        selectedEntity = null;
        selectedActionType = null;
        selectedStaffName = null;
        startDate = null;
        endDate = null;
        filterAuditLogs(); // Reload all logs after clearing filters
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public String getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(String selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public String getSelectedActionType() {
        return selectedActionType;
    }

    public void setSelectedActionType(String selectedActionType) {
        this.selectedActionType = selectedActionType;
    }

    public String getSelectedStaffName() {
        return selectedStaffName;
    }

    public void setSelectedStaffName(String selectedStaffName) {
        this.selectedStaffName = selectedStaffName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(java.time.LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(java.time.LocalDateTime endDate) {
        this.endDate = endDate;
    }
}

