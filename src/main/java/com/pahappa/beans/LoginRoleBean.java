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
        String result = null;
        try {
            if ("doctor".equals(role)) {
                authBean.reset();
                doctorAuthBean.setEmail(email);
                doctorAuthBean.setPassword(password);
                System.out.println("The username is " + doctorAuthBean.getEmail() + " and the password is " + doctorAuthBean.getPassword());
                result = doctorAuthBean.login();
                System.out.println("The result is " + result);
            } else {
                doctorAuthBean.reset();
                authBean.setEmail(email);
                authBean.setPassword(password);
                result = authBean.login();
            }

            if (result != null) {
                // On SUCCESS, append the redirect parameter and navigate.
                return result + "?faces-redirect=true";
            } else {
                // On FAILURE, add the message and return null to stay on the same page.
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials or account is inactive."));
                return null;
            }

        } catch (Exception e) {
            // Catch any unexpected errors from the backend
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Error", "An unexpected error occurred. Please contact support."));
            e.printStackTrace();
            return null;
        }
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

