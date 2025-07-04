package com.pahappa.beans;

import com.pahappa.models.Staff;
import com.pahappa.models.Doctor;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginRoleBean implements Serializable {
    private String role = "staff";
    private String email;
    private String password;

    @Inject
    private AuthBean authBean;
    @Inject
    private DoctorAuthBean doctorAuthBean;

    public String login() {
        if ("doctor".equals(role)) {
            doctorAuthBean.setEmail(email);
            doctorAuthBean.setPassword(password);
            String result = doctorAuthBean.login();
            if (result == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid doctor credentials or account deleted."));
            }
            return result;
        } else {
            authBean.setEmail(email);
            authBean.setPassword(password);
            String result = authBean.login();
            if (result == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid staff credentials or account deleted."));
            }
            return result;
        }
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

