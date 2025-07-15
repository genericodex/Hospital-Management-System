package com.pahappa.beans;

import com.pahappa.dao.RoleDao;
import com.pahappa.models.Permissions;
import com.pahappa.models.Role;
import com.pahappa.services.permissions.PermissionService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

@Named
@ViewScoped
public class SettingsBean implements Serializable {
    private String newSpecialization;
    private String newRole;
    private List<Role> roles;
    private Role selectedRole;
    private DualListModel<Permissions> permissionsModel;// For the input field
    private List<String> specializations = new ArrayList<>();
    private List<Permissions> permissions;
    private Permissions newPermission;

    @Inject // <-- FIX: Use CDI to inject the service
    private transient PermissionService permissionService;

    @Inject // Inject RoleDao for role management
    private transient RoleDao roleDao;

    @Inject
    private transient AuthBean authBean;

    // Preferences for specializations (leaving as-is)
    private static final String SPECIALIZATIONS_KEY = "specializations";
    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    @PostConstruct
    public void init() {
        // Load roles from the database
        this.permissions = permissionService.getAllPermissions();
        this.newPermission = new Permissions();
        // Load specializations from preferences
        String specs = prefs.get(SPECIALIZATIONS_KEY, null);
        if (specs != null && !specs.isEmpty()) {
            specializations = new ArrayList<>(List.of(specs.split(",")));
        }
        roles = permissionService.getAllRoles();
        // Initialize with all permissions in the source list
        permissionsModel = new DualListModel<>(permissionService.getAllPermissions(), new ArrayList<>());
    }

    private void saveSpecializations() {
        prefs.put(SPECIALIZATIONS_KEY, String.join(",", specializations));
    }

    // --- Getters and Setters ---
    public String getNewSpecialization() { return newSpecialization; }
    public void setNewSpecialization(String newSpecialization) { this.newSpecialization = newSpecialization; }
    public String getNewRole() { return newRole; }
    public void setNewRole(String newRole) { this.newRole = newRole; }
    public List<String> getSpecializations() { return specializations; }
    public List<Role> getRoles() { return roles; }
    public Role getSelectedRole() { return selectedRole; }
    public void setSelectedRole(Role selectedRole) { this.selectedRole = selectedRole; }
    public DualListModel<Permissions> getPermissionsModel() { return permissionsModel; }
    public void setPermissionsModel(DualListModel<Permissions> permissionsModel) { this.permissionsModel = permissionsModel; }
    public List<Permissions> getPermissions() {return permissions;}

    public Permissions getNewPermission() {return newPermission;}

    public void setNewPermission(Permissions newPermission) {this.newPermission = newPermission;}
    // --- Specialization Methods (Unchanged) ---
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

    // --- Role Methods (Updated) ---
    public void addRole() {
        if (!authBean.hasPermission("MANAGE_ROLES")) {
            // You can add a general access denied message helper
            return;
        }
        if (newRole != null && !newRole.trim().isEmpty()) {
            // Check if role already exists (case-insensitive check)
            boolean roleExists = roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(newRole.trim()));
            if (!roleExists) {
                Role role = new Role(newRole.trim().toUpperCase());
                roleDao.createRole(role);
                roles = roleDao.findAll(); // Refresh the list from DB
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Role added", "The role '" + role.getName() + "' has been added."));
                newRole = "";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicate Role", "The role '" + newRole.trim() + "' already exists."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Input", "Role name cannot be empty."));
        }
    }

    public void removeRole(Role role) {
        try {
            roleDao.deleteRole(role);
            roles = roleDao.findAll(); // Refresh the list from DB
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Role removed", "The role '" + role.getName() + "' has been removed."));
        } catch (Exception e) {
            // This will catch constraint violation errors if the role is in use
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Removing Role", "Could not remove role. It may be in use by a staff member."));
        }
    }

    public void onRoleSelect() {
        if (selectedRole != null) {
            // Fetch the role again to ensure its permissions are loaded
            Role freshRole = permissionService.getAllRoles().stream()
                    .filter(r -> r.getId().equals(selectedRole.getId()))
                    .findFirst().orElse(null);

            if (freshRole != null) {
                List<Permissions> source = permissionService.getAllPermissions();
                List<Permissions> target = new ArrayList<>(freshRole.getPermissions());
                source.removeAll(target);
                permissionsModel = new DualListModel<>(source, target);
            }
        } else {
            // If no role is selected, reset the picklist
            permissionsModel = new DualListModel<>(permissionService.getAllPermissions(), new ArrayList<>());
        }
    }

    public void savePermissions() {
        if (!authBean.hasPermission("MANAGE_PERMISSIONS")) {
            return;
        }

        if (selectedRole != null) {
            try {
                permissionService.updatePermissionsForRole(selectedRole, permissionsModel.getTarget());
                // Refresh the selected role's permissions to reflect the change
                selectedRole.getPermissions().clear();
                selectedRole.getPermissions().addAll(permissionsModel.getTarget());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Permissions updated for " + selectedRole.getName()));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update permissions."));
                e.printStackTrace();
            }
        }
    }
    public void createPermission() {
        if (newPermission.getPermissionKey() == null || newPermission.getPermissionKey().isBlank() ||
                newPermission.getDescription() == null || newPermission.getDescription().isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Validation Error", "Key and Description cannot be empty."));
            return;
        }

        try {
            // Optional: Check if key already exists
            boolean keyExists = permissions.stream().anyMatch(p -> p.getPermissionKey().equalsIgnoreCase(newPermission.getPermissionKey()));
            if (keyExists) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "A permission with this key already exists."));
                return;
            }

            permissionService.createPermission(newPermission);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Permission created successfully."));

            // Reset for next creation
            init();
            PrimeFaces.current().ajax().update("permissionForm:permissionsTable"); // Refresh the table
            PrimeFaces.current().ajax().update("permissionForm:createPanel"); // Refresh the input fields
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Error", "Could not create permission."));
        }
    }
    public void deletePermission(Permissions permissionToDelete) {
        try {
            permissionService.deletePermission(permissionToDelete);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Permission '" + permissionToDelete.getPermissionKey() + "' deleted."));
            // Refresh the list from the database
            permissions.remove(permissionToDelete);
        } catch (Exception e) {
            // This can happen if the permission is still linked to a role (foreign key constraint)
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete permission. It may be in use by a role."));
        }
    }
}
