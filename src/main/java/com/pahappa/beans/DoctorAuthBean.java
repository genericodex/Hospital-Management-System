package com.pahappa.beans;

import com.pahappa.dao.DoctorDao;
import com.pahappa.models.Doctor;
import com.pahappa.services.AppointmentServiceImpl;
import com.pahappa.services.HospitalService;
import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import com.pahappa.constants.Specialization;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class DoctorAuthBean implements Serializable {
    private String email;
    private String password;
    private Doctor loggedInDoctor;
    private boolean editMode = false;

    public String login() {
        DoctorDao doctorDao = new DoctorDao();
        loggedInDoctor = doctorDao.authenticate(email, password);
        if (loggedInDoctor != null) {
            // Log login event (like AuthBean)
            AuditLogDao auditLogDao = new com.pahappa.dao.AuditLogDao();
            AuditLog log = new com.pahappa.models.AuditLog();
            log.setActionType("LOGIN");
            log.setEntityName("Doctor");
            log.setEntityId(String.valueOf(loggedInDoctor.getId()));
            log.setStaffId(loggedInDoctor.getId());
            log.setStaffName("Dr. " + loggedInDoctor.getFirstName() + " " + loggedInDoctor.getLastName());
            log.setTimestamp(java.time.LocalDateTime.now());
            log.setNewValue(loggedInDoctor.toString());
            auditLogDao.saveAuditLog(log);
            // Put bean in session map for EL compatibility
            jakarta.faces.context.FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("doctorAuthBean", this);
            return "/doctor-dashboard.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials or account deleted."));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public int getAppointmentCount() {
        if (loggedInDoctor == null) return 0;
        HospitalService hospitalService = new AppointmentServiceImpl();
        List<?> appointments = hospitalService.getAppointmentsByDoctor(loggedInDoctor.getId());
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
            DoctorDao doctorDao = new DoctorDao();
            doctorDao.updateDoctor(loggedInDoctor);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile Updated", "Your profile has been updated successfully."));
            this.editMode = false;
        }
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
}
