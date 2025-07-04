package com.pahappa.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

@Named
@SessionScoped
public class SettingsBean implements Serializable {
    private String newSpecialization;
    private String newRole;
    private List<String> specializations = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    private static final String SPECIALIZATIONS_KEY = "specializations";
    private static final String ROLES_KEY = "roles";
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    @PostConstruct
    public void init() {
        // Load from preferences (or DB in a real app)
        String specs = prefs.get(SPECIALIZATIONS_KEY, null);
        String rolesStr = prefs.get(ROLES_KEY, null);
        if (specs != null) {
            specializations = new ArrayList<>(List.of(specs.split(",")));
        }
        if (rolesStr != null) {
            roles = new ArrayList<>(List.of(rolesStr.split(",")));
        }
    }

    private void saveSpecializations() {
        prefs.put(SPECIALIZATIONS_KEY, String.join(",", specializations));
    }
    private void saveRoles() {
        prefs.put(ROLES_KEY, String.join(",", roles));
    }

    public String getNewSpecialization() {
        return newSpecialization;
    }
    public void setNewSpecialization(String newSpecialization) {
        this.newSpecialization = newSpecialization;
    }
    public String getNewRole() {
        return newRole;
    }
    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }
    public List<String> getSpecializations() {
        return specializations;
    }
    public List<String> getRoles() {
        return roles;
    }

    public void addSpecialization() {
        if (newSpecialization != null && !newSpecialization.trim().isEmpty() && !specializations.contains(newSpecialization.trim().toUpperCase())) {
            specializations.add(newSpecialization.trim().toUpperCase());
            saveSpecializations();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Specialization added", null));
            newSpecialization = "";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid or duplicate specialization", null));
        }
    }

    public void removeSpecialization(String spec) {
        specializations.remove(spec);
        saveSpecializations();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Specialization removed", null));
    }

    public void addRole() {
        if (newRole != null && !newRole.trim().isEmpty() && !roles.contains(newRole.trim())) {
            roles.add(newRole.trim());
            saveRoles();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Role added", null));
            newRole = "";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid or duplicate role", null));
        }
    }

    public void removeRole(String role) {
        roles.remove(role);
        saveRoles();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Role removed", null));
    }
}
