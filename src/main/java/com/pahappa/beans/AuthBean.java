package com.pahappa.beans;

import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import com.pahappa.models.Permissions;
import com.pahappa.models.Staff;
import com.pahappa.services.staff.StaffService;
import com.pahappa.util.PasswordEncoder;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class AuthBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private Staff staff; // Renamed from loggedInUser
    private String currentPassword;
    private boolean showPasswordChangeForm = false;
    private String newPassword;
    private String confirmPassword;

    @Inject
    private transient AuditLogDao auditLogDao;
    @Inject
    private transient PasswordEncoder passwordEncoder;

    @Inject
    private transient StaffService staffService;
    private Set<String> userPermissions;

    private boolean editMode = false;

    public String login() {
        try {
            staff = staffService.authenticateStaff(email, password);

            if (staff != null) {
                // Log login event
                AuditLog log = new AuditLog();
                log.setActionType("LOGIN");
                log.setEntityName("Staff");
                log.setEntityId(String.valueOf(staff.getId()));
                log.setStaffId(staff.getId());
                log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
                log.setTimestamp(LocalDateTime.now());
                log.setNewValue(staff.toString());
                auditLogDao.saveAuditLog(log);
                loadUserPermissions();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authBean", this);
                return "/Dashboards/dashboard";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed. \n", "Invalid credentials"));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Error", e.getMessage()));
            return null;
        }
    }

    public String logout() {
        // Log logout event if user is logged in
        if (staff != null) {
            AuditLog log = new AuditLog();
            log.setActionType("LOGOUT");
            log.setEntityName("Staff");
            log.setEntityId(String.valueOf(staff.getId()));
            log.setStaffId(staff.getId());
            log.setStaffName(staff.getFirstName() + " " + staff.getLastName());
            log.setTimestamp(LocalDateTime.now());
            log.setOldValue(staff.toString());
            auditLogDao.saveAuditLog(log);
        }
        this.userPermissions = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/Auth/login.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return staff != null;
    }

    public void toggleEditMode() {
        this.editMode = !this.editMode;
    }

    public void saveProfile() {
        if (staff != null) {
            staffService.updateStaff(staff, staff);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile Updated", "Your profile has been updated successfully."));
            this.editMode = false;
        }
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Staff getStaff() { return staff; } // Renamed from getLoggedInUser

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }


    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public boolean isShowPasswordChangeForm() { return showPasswordChangeForm; }

    // Helper method to simplify adding messages
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    private void loadUserPermissions() {
        this.userPermissions = new HashSet<>();
        if (staff != null && staff.getRole() != null && staff.getRole().getPermissions() != null) {
            this.userPermissions = staff.getRole().getPermissions().stream()
                    .map(Permissions::getPermissionKey)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Checks if the logged-in user has a specific permission.
     * This is the method you will use throughout your application's front end.
     * @param key The permission key (e.g., "PATIENT_DELETE")
     * @return true if the user has the permission, false otherwise.
     */
    public boolean hasPermission(String key) {
        if (userPermissions == null) {
            return false;
        }
        // Add a check for a super-admin role if you want one
        if (userPermissions.contains("ADMIN_ACCESS_ALL")) {
            return true;
        }
        return userPermissions.contains(key);
    }
    // --- FIX: Method to show/hide the password change form ---
    public void togglePasswordChange() {
        this.showPasswordChangeForm = !this.showPasswordChangeForm;
        // Clear fields when hiding the form for security
        if (!this.showPasswordChangeForm) {
            this.currentPassword = null;
            this.newPassword = null;
            this.confirmPassword = null;
        }
    }

    // --- FIX: The core logic for changing the password ---
    public void changePassword() {
        // 1. Basic validation: Do the new passwords match?
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Password Mismatch", "The new passwords do not match.");
            return;
        }

        // 2. Security check: Is the current password correct?
        if (!passwordEncoder.matches(currentPassword, staff.getPassword())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Incorrect Password", "The current password you entered is incorrect.");
            return;
        }

        // 3. All checks passed, proceed to change the password via the service
        try {
            // The service layer will handle hashing and saving
            staffService.changePassword(staff.getId(), currentPassword, newPassword);

            // Refresh the user's state in the session
            this.staff = staffService.getStaffById(staff.getId());

            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your password has been changed successfully.");
            togglePasswordChange(); // Hide the form on success
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_FATAL, "System Error", "Could not change password due to a system error.");
            e.printStackTrace();
        }
    }

    public void reset() {
        this.email = null;
        this.password = null;
        this.staff = null;
        this.currentPassword = null;
        this.newPassword = null;
        this.confirmPassword = null;
    }
}
