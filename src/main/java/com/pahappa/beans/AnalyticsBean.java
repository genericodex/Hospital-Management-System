package com.pahappa.beans;

import com.pahappa.models.analytics.*;
import com.pahappa.services.analytics.AnalyticsService;
import com.pahappa.services.dashboard.DashboardScheduler;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AnalyticsBean implements Serializable {

    @Inject
    private AnalyticsService analyticsService;

    @Inject
    private DashboardScheduler dashboardScheduler; // Inject the scheduler

    private List<DataPoint> dataPoints;
    private DataPoint newDataPoint;

    private List<AnalysisDimension> analysisDimensions;
    private AnalysisDimension newDimension;
    private Long selectedDataPointId;

    private List<CurrentView> currentViews;
    private List<BenchmarkLog> benchmarkLogs;

    @PostConstruct
    public void init() {
        newDataPoint = new DataPoint();
        newDimension = new AnalysisDimension();
        refreshData();
    }

    public void refreshData() {
        dataPoints = analyticsService.getAllDataPoints();
        analysisDimensions = analyticsService.getAllAnalysisDimensions();
        currentViews = analyticsService.getAllCurrentViews();
        benchmarkLogs = analyticsService.getRecentBenchmarkLogs();
    }

    // --- NEW: Manual Trigger ---
    public void runAnalysisNow() {
        try {
            dashboardScheduler.forceRun();
            refreshData();
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Analysis triggered manually.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to run analysis: " + e.getMessage());
        }
    }

    public void saveDataPoint() {
        try {
            analyticsService.saveDataPoint(newDataPoint);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Data Point saved.");
            newDataPoint = new DataPoint();
            refreshData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save Data Point: " + e.getMessage());
        }
    }

    public void deleteDataPoint(DataPoint dp) {
        try {
            analyticsService.deleteDataPoint(dp.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Data Point deleted.");
            refreshData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cannot delete Data Point. It might be in use.");
        }
    }

    public void saveDimension() {
        try {
            if (selectedDataPointId != null) {
                DataPoint dp = dataPoints.stream()
                        .filter(d -> d.getId().equals(selectedDataPointId))
                        .findFirst()
                        .orElse(null);
                newDimension.setDataPoint(dp);
            }
            analyticsService.saveAnalysisDimension(newDimension);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Analysis Dimension saved.");
            newDimension = new AnalysisDimension();
            selectedDataPointId = null;
            refreshData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save Dimension: " + e.getMessage());
        }
    }

    public void deleteDimension(AnalysisDimension ad) {
        try {
            analyticsService.deleteAnalysisDimension(ad.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Dimension deleted.");
            refreshData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public List<DataPoint> getDataPoints() { return dataPoints; }
    public DataPoint getNewDataPoint() { return newDataPoint; }
    public void setNewDataPoint(DataPoint newDataPoint) { this.newDataPoint = newDataPoint; }
    public List<AnalysisDimension> getAnalysisDimensions() { return analysisDimensions; }
    public AnalysisDimension getNewDimension() { return newDimension; }
    public void setNewDimension(AnalysisDimension newDimension) { this.newDimension = newDimension; }
    public Long getSelectedDataPointId() { return selectedDataPointId; }
    public void setSelectedDataPointId(Long selectedDataPointId) { this.selectedDataPointId = selectedDataPointId; }
    public List<CurrentView> getCurrentViews() { return currentViews; }
    public List<BenchmarkLog> getBenchmarkLogs() { return benchmarkLogs; }
}