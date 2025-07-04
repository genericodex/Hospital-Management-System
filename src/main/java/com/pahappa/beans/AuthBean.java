package com.pahappa.beans;

import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import com.pahappa.models.Staff;
import com.pahappa.services.HospitalService;
import com.pahappa.services.StaffServiceImpl;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Named
@SessionScoped
public class AuthBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private Staff loggedInUser;
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public String login() {
        try {
            loggedInUser = new StaffServiceImpl().authenticateStaff(email, password);

            if (loggedInUser != null) {
                // Log login event
                AuditLog log = new AuditLog();
                log.setActionType("LOGIN");
                log.setEntityName("Staff");
                log.setEntityId(String.valueOf(loggedInUser.getId()));
                log.setStaffId(loggedInUser.getId());
                log.setStaffName(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
                log.setTimestamp(LocalDateTime.now());
                log.setNewValue(loggedInUser.toString());
                auditLogDao.saveAuditLog(log);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authBean", this);
                return "/dashboard.xhtml?faces-redirect=true";
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
        if (loggedInUser != null) {
            AuditLog log = new AuditLog();
            log.setActionType("LOGOUT");
            log.setEntityName("Staff");
            log.setEntityId(String.valueOf(loggedInUser.getId()));
            log.setStaffId(loggedInUser.getId());
            log.setStaffName(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
            log.setTimestamp(LocalDateTime.now());
            log.setOldValue(loggedInUser.toString());
            auditLogDao.saveAuditLog(log);
        }
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Staff getLoggedInUser() { return loggedInUser; }
}