package com.pahappa.beans;

import com.pahappa.constants.StaffRoles;
import com.pahappa.models.Staff;
import com.pahappa.services.StaffServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.hibernate.annotations.View;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class StaffBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private StaffServiceImpl staffService;

    @Inject
    private SettingsBean settingsBean;

    private List<Staff> staffList;
    private List<Staff> selectedStaffList;
    private Staff selectedStaff;
    private String password;
    private boolean newStaff;
    private Long staffToDeleteId;
    private String searchTerm;
    private List<Staff> filteredStaffList;
    private List<Staff> deletedStaff;

    @PostConstruct
    public void init() {
        staffList = staffService.getAllActiveStaff();
        selectedStaff = new Staff();
    }

    public void initNewStaff() {
        selectedStaff = new Staff();
        password = null;
        newStaff = true;
    }

    public void editStaff(Staff staff) {
        // Always fetch a fresh, managed entity from the DB for editing
        this.selectedStaff = staffService.getStaffById(staff.getId());
        this.newStaff = false;
    }

    public void saveStaff() {
        try {
            if (selectedStaff.getId() == null) {
                staffService.createStaff(
                        selectedStaff.getFirstName(),
                        selectedStaff.getLastName(),
                        selectedStaff.getEmail(),
                        selectedStaff.getContactNumber(),
                        selectedStaff.getRole(),
                        selectedStaff.getDepartment(),
                        selectedStaff.getHireDate(),
                        password,
                        false,
                        null
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Staff member created successfully"));
            } else {
                Staff managedStaff = staffService.getStaffById(selectedStaff.getId());
                if (managedStaff != null) {
                    managedStaff.setFirstName(selectedStaff.getFirstName());
                    managedStaff.setLastName(selectedStaff.getLastName());
                    managedStaff.setEmail(selectedStaff.getEmail());
                    managedStaff.setContactNumber(selectedStaff.getContactNumber());
                    managedStaff.setRole(selectedStaff.getRole());
                    managedStaff.setDepartment(selectedStaff.getDepartment());
                    managedStaff.setHireDate(selectedStaff.getHireDate());
                    managedStaff.setPassword(selectedStaff.getPassword());
                    managedStaff.setDeleted(selectedStaff.isDeleted());
                    // Pass current user as actor for audit log
                    staffService.updateStaff(managedStaff, selectedStaff);
                }
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Staff member updated successfully"));
            }
            staffList = staffService.getAllActiveStaff();
            selectedStaff = new Staff();
            password = null;
            newStaff = false;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteStaff(Staff staff) {
        try {
            // Pass current user as actor for audit log
            staffService.softDeleteStaff(staff.getId(), selectedStaff);
            staffList = staffService.getAllActiveStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Staff member deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteSelectedStaff() {
        try {
            for (Staff staff : selectedStaffList) {
                staffService.softDeleteStaff(staff.getId(), selectedStaff);
            }
            staffList = staffService.getAllActiveStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedStaffList.size() + " staff members deleted successfully"));
            selectedStaffList = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void restoreStaff(Long id) {
        try {
            staffService.restoreStaff(id, selectedStaff);
            staffList = staffService.getAllActiveStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Staff member restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void hardDeleteStaff(Long id) {
        try {
            staffService.deleteStaff(id, selectedStaff);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Staff member permanently deleted"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void searchStaff() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            filteredStaffList = new java.util.ArrayList<>(staffList);
        } else {
            String lower = searchTerm.toLowerCase();
            filteredStaffList = staffList.stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(lower)
                    || (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(lower)))
                .toList();
        }
    }

    public void loadDeletedStaff() {
        deletedStaff = staffService.getDeletedStaff();
    }

    public StaffRoles[] getRoles() {
        return StaffRoles.values();
    }

    public List<String> getAvailableRoles() {
        return settingsBean.getRoles();
    }

    public List<String> getAvailableSpecializations() {
        return settingsBean.getSpecializations();
    }

    // Getters and Setters
    public List<Staff> getStaffList() { return staffList; }
    public List<Staff> getSelectedStaffList() { return selectedStaffList; }
    public void setSelectedStaffList(List<Staff> selectedStaffList) { this.selectedStaffList = selectedStaffList; }
    public Staff getSelectedStaff() { return selectedStaff; }
    public void setSelectedStaff(Staff selectedStaff) { this.selectedStaff = selectedStaff; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isNewStaff() { return newStaff; }
    public Long getStaffToDeleteId() { return staffToDeleteId; }
    public void setStaffToDeleteId(Long staffToDeleteId) { this.staffToDeleteId = staffToDeleteId; }
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public List<Staff> getFilteredStaffList() {
        if (filteredStaffList == null) {
            filteredStaffList = new ArrayList<>(staffList);
        }
        return filteredStaffList;
    }
    public List<Staff> getDeletedStaff() {
        return deletedStaff;
    }
}
