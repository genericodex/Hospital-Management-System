package com.pahappa.beans;

import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.util.stream.Collectors;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void loadChartData() {
            List<Object[]> userActivityResults = auditLogDao.getActionCountsByStaff();
        // Re-implementing with manual string building to match DashboardBean
        String userActivityJson = userActivityResults.stream()
                .map(row -> {
                    String staffName = (String) row[0];
                    // FIX: Handle cases where the staff name might be null in the database.
                    if (staffName == null) {
                        staffName = "System Action"; // Provide a default for null names
                    }
                    // Escape quotes in names to prevent breaking the JSON string
                    String escapedName = staffName.replace("\"", "\\\"");
                    Long count = (Long) row[1];
                    return String.format("{\"staffName\": \"%s\", \"count\": %d}", escapedName, count);
                })
                .collect(Collectors.joining(", ", "[", "]"));

        // 2. Data for Action Type Breakdown Chart
        List<Object[]> actionTypeResults = auditLogDao.getActionTypeCounts();
        String actionTypeJson = actionTypeResults.stream()
                .map(row -> {
                    String actionType = (String) row[0];
                    // FIX: Handle cases where the action type might be null in the database.
                    if (actionType == null) {
                        actionType = "Other"; // Provide a default for null types
                    }Long count = (Long) row[1];
                    // Escape quotes just in case
                    String escapedAction = (actionType != null) ? actionType.replace("\"", "\\\"") : "Other";
                    return String.format("{\"action\": \"%s\", \"count\": %d}", escapedAction, count);
                })
                .collect(Collectors.joining(", ", "[", "]"));

        // Pass data to JavaScript via PrimeFaces args to render the chart
        PrimeFaces.current().executeScript("renderAuditChart(" + userActivityJson + ", " + actionTypeJson + ");");
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

