package com.pahappa.beans;

import com.pahappa.dao.DoctorDao;
import com.pahappa.models.Doctor;
import com.pahappa.services.appointment.AppointmentService;
import com.pahappa.services.appointment.impl.AppointmentServiceImpl;
import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import com.pahappa.constants.Specialization;
import com.pahappa.services.doctor.DoctorService;
import com.pahappa.util.PasswordEncoder;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class DoctorAuthBean implements Serializable {

    @Inject
    private transient AppointmentService appointmentService;
    @Inject
    private transient DoctorService doctorService; // <-- FIX: Inject the service


    private String email;
    private String password;
    private Doctor loggedInDoctor;
    private boolean editMode = false;
    // --- FIX: Fields for the password change form ---
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    private boolean showPasswordChangeForm = false;

    public void reset() {
        this.email = null;
        this.password = null;
        this.loggedInDoctor = null;
        this.editMode = false;
        this.currentPassword = null;
        this.newPassword = null;
        this.confirmPassword = null;
        this.showPasswordChangeForm = false;
    }

    public String login() {
        try {
            // --- FIX: Use a dedicated, secure authentication method from the service ---
            // This logic is now moved to the service layer.
            Doctor doctor = doctorService.authenticateDoctor(email, password);

            if (doctor != null) {
                this.loggedInDoctor = doctor;
                // ... (rest of your successful login logic: audit log, etc.) ...
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("doctorAuthBean", this);
                return "/Doctors/doctor-dashboard";

            } else {
                // If login fails, show a message and return null.
                addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials or account deleted.");
                System.out.println("");
                return null;
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Login Error", "An unexpected error occurred during login.");
            e.printStackTrace();
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public int getAppointmentCount() {
        if (loggedInDoctor == null) return 0;
        List<?> appointments = appointmentService.getAppointmentsByDoctor(loggedInDoctor.getId());
        return appointments != null ? appointments.size() : 0;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void toggleEditMode() {
        this.editMode = !this.editMode;
    }

    public void saveProfile() {
        if (loggedInDoctor != null) {
            doctorService.updateDoctor(loggedInDoctor);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile Updated", "Your profile has been updated successfully."));
            this.editMode = false;
        }
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
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Password Mismatch", "The new passwords do not match.");
            return;
        }

        try {
            // Delegate the entire logic to the service layer
            doctorService.changePassword(loggedInDoctor.getId(), currentPassword, newPassword);

            // Refresh the user's state in the session
            this.loggedInDoctor = doctorService.getDoctorById(loggedInDoctor.getId());

            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your password has been changed successfully.");
            togglePasswordChange(); // Hide the form on success
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            e.printStackTrace();
        }
    }
    // Helper method to simplify adding messages
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public List<SelectItem> getSpecializationOptions() {
        List<SelectItem> options = new ArrayList<>();
        for (Specialization spec : Specialization.values()) {
            options.add(new SelectItem(spec, spec.name()));
        }
        return options;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Doctor getLoggedInDoctor() { return loggedInDoctor; }
    public void setLoggedInDoctor(Doctor loggedInDoctor) { this.loggedInDoctor = loggedInDoctor; }
    // --- FIX: Add Getters and Setters for the new fields ---
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public boolean isShowPasswordChangeForm() { return showPasswordChangeForm; }
}
