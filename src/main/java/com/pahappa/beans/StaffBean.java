package com.pahappa.beans;

import com.pahappa.dao.RoleDao;
import com.pahappa.models.Role;
import com.pahappa.models.Staff;
import com.pahappa.services.staff.StaffService;
import com.pahappa.util.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
    private StaffService staffService;

    @Inject
    private AuthBean authBean; // Injected to get the current user's role

    // Let the container manage all my components, so I inject the DAO
    // instead of creating it manually with `new`.
    @Inject
    private RoleDao roleDao;

    @Inject
    private PasswordEncoder passwordEncoder;
    private List<Role> availableRoles;

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
        this.selectedStaff = new Staff();
        try {
            // Load all necessary data ONCE.
            staffList = staffService.getAllActiveStaff();
            filteredStaffList = new ArrayList<>(staffList);
            availableRoles = roleDao.findAll(); // Assumes you add this to your service
            deletedStaff = new ArrayList<>(); // Initialize to prevent nulls
        } catch (Exception e) {
            // Prevent page from crashing if DB is down
            staffList = new ArrayList<>();
            availableRoles = new ArrayList<>();
            filteredStaffList = new ArrayList<>();
            deletedStaff = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not load staff data."));
        }
    }
    // --- Helper methods for security ---
    private boolean isAdmin() {
        if (authBean != null && authBean.getStaff() != null && authBean.getStaff().getRole() != null) {
            return "ADMIN".equals(authBean.getStaff().getRole().getName())|| "IT SUPPORT".equals(authBean.getStaff().getRole().getName());
        }
        return false;
    }

    private void showAccessDeniedMessage() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Access Denied", "You do not have permission to perform this action."));
    }

    // --- Action Methods ---

    public void initNewStaff() {
        selectedStaff = new Staff();
        password = null;
        newStaff = true;
    }

    public void editStaff(Staff staff) {
        this.selectedStaff = staffService.getStaffById(staff.getId());
        this.password = null;
        this.newStaff = false;
    }

    public void saveStaff() {
        if (!authBean.hasPermission("STAFF_EDIT")) {
            showAccessDeniedMessage();
            return;
        }
        try {
            if (newStaff) {
                // For a NEW staff member, the service layer is responsible for hashing.
                staffService.createStaff(
                        selectedStaff.getFirstName(),
                        selectedStaff.getLastName(),
                        selectedStaff.getEmail(),
                        selectedStaff.getContactNumber(),
                        selectedStaff.getRole(),
                        selectedStaff.getDepartment(),
                        selectedStaff.getHireDate(),
                        password, // Pass the plain-text password for creation
                        false,
                        null
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Staff member created successfully"));
            } else {
                // For an EXISTING staff member, check if a new password was provided.
                if (password != null && !password.trim().isEmpty()) {
                    // If yes, hash it here in the bean and set it on the object.
                    String hashedPassword = passwordEncoder.encode(password);
                    selectedStaff.setPassword(hashedPassword);
                }
                // Now, update the staff member. The password will either be the new hash
                // or the original, untouched hash that was already on the selectedStaff object.
                staffService.updateStaff(selectedStaff, authBean.getStaff());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Staff member " + selectedStaff.getFirstName() + " updated successfully"));
            }
            init();
            password = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteStaff(Staff staff) {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            staffService.softDeleteStaff(staff.getId(), authBean.getStaff()); // Pass current user as actor
            staffList = staffService.getAllActiveStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Staff member deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
        init();
    }

    public void deleteSelectedStaff() {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            for (Staff staff : selectedStaffList) {
                staffService.softDeleteStaff(staff.getId(), authBean.getStaff()); // Pass current user as actor
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
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            staffService.restoreStaff(id, authBean.getStaff()); // Pass current user as actor
            staffList = staffService.getAllActiveStaff();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Staff member restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void hardDeleteStaff(Long id) {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            staffService.deleteStaff(id, authBean.getStaff()); // Pass current user as actor
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
                    || (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(lower) || (s.getRole() != null && s.getRole().getName().toLowerCase().contains(lower))))
                .toList();
        }
    }

    public void loadDeletedStaff() {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            // Also clear the list to prevent showing data from a previous admin session
            deletedStaff = new ArrayList<>();
            return;
        }
        deletedStaff = staffService.getDeletedStaff();
    }

    public List<Role> getAvailableRoles() {
        return availableRoles;
    }

    // --- Getters and Setters ---
    public List<Staff> getStaffList() {
        if (staffList == null || staffList.isEmpty()) {
            staffList = staffService.getAllActiveStaff();
        }
        return staffList;
    }
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
        return filteredStaffList;
    }
    public List<Staff> getDeletedStaff() {
        if (deletedStaff == null) {
            deletedStaff = new ArrayList<>(); // Initialize to avoid nulls
        }
        return deletedStaff;
    }


}
