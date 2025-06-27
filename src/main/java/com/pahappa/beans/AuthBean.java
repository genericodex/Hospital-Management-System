package com.pahappa.beans;

import com.pahappa.models.Staff;
import com.pahappa.services.HospitalService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;

@Named
@SessionScoped
public class AuthBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private Staff loggedInUser;

    public String login() {
        try {
            loggedInUser = new HospitalService().authenticateStaff(email, password);

            if (loggedInUser != null) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authBean", this);
                return "/dashboard.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials"));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Error", e.getMessage()));
            return null;
        }
    }

    public String logout() {
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