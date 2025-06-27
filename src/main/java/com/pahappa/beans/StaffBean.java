package com.pahappa.beans;

import com.pahappa.constants.StaffRoles;
import com.pahappa.models.Staff;
import com.pahappa.services.HospitalService;
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
public class StaffBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private HospitalService hospitalService;

    private List<Staff> staffList;
    private List<Staff> selectedStaffList;
    private List<Staff> deletedStaff;
    private Staff selectedStaff;
    private String password;
    private boolean newStaff;

    @PostConstruct
    public void init() {
        staffList = hospitalService.getAllActiveStaff();
        selectedStaff = new Staff();
    }

    public void initNewStaff() {
        selectedStaff = new Staff();
        password = null;
        newStaff = true;
    }

    public void editStaff(Staff staff) {
        // Always fetch a fresh, managed entity from the DB for editing
        this.selectedStaff = hospitalService.getStaffById(staff.getId());
        this.newStaff = false;
    }

    public void saveStaff() {
        try {
            if (selectedStaff.getId() == null) {
                hospitalService.createStaff(
                        selectedStaff.getFirstName(),
                        selectedStaff.getLastName(),
                        selectedStaff.getEmail(),
                        selectedStaff.getContactNumber(),
                        selectedStaff.getRole(),
                        selectedStaff.getDepartment(),
                        selectedStaff.getHireDate(),
                        password,
                        false
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Staff member created successfully"));
            } else {
                // Always fetch managed entity for update
                Staff managedStaff = hospitalService.getStaffById(selectedStaff.getId());
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
                    hospitalService.updateStaff(managedStaff);
                }
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Staff member updated successfully"));
            }
            staffList = hospitalService.getAllActiveStaff();
            // Reset selectedStaff and newStaff after save/update
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
            hospitalService.softDeleteStaff(staff.getId());
            staffList = hospitalService.getAllActiveStaff();
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
                hospitalService.softDeleteStaff(staff.getId());
            }
            staffList = hospitalService.getAllActiveStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedStaffList.size() + " staff members deleted successfully"));
            selectedStaffList = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadDeletedStaff() {
        deletedStaff = hospitalService.getDeletedStaff();
    }

    public void restoreStaff(Long id) {
        try {
            hospitalService.restoreStaff(id);
            staffList = hospitalService.getAllActiveStaff();
            deletedStaff = hospitalService.getDeletedStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Staff member restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public StaffRoles[] getRoles() {
        return StaffRoles.values();
    }

    // Getters and Setters
    public List<Staff> getStaffList() { return staffList; }
    public List<Staff> getSelectedStaffList() { return selectedStaffList; }
    public void setSelectedStaffList(List<Staff> selectedStaffList) { this.selectedStaffList = selectedStaffList; }
    public Staff getSelectedStaff() { return selectedStaff; }
    public void setSelectedStaff(Staff selectedStaff) { this.selectedStaff = selectedStaff; }
    public List<Staff> getDeletedStaff() { return deletedStaff; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isNewStaff() { return newStaff; }
}