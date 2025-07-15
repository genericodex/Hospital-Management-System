package com.pahappa.services.permissions;

import com.pahappa.dao.PermissionDao;
import com.pahappa.dao.RoleDao;
import com.pahappa.models.Permissions;
import com.pahappa.models.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PermissionService {
    @Inject
    private  RoleDao roleDao;

    @Inject
    private  PermissionDao permissionDao;

    public List<Role> getAllRoles() {
        return roleDao.findAllWithPermissions();
    }

    public List<Permissions> getAllPermissions() {
        return permissionDao.findAll();
    }

    @Transactional
    public void updatePermissionsForRole(Role role, List<Permissions> newPermissions) {
        // Use the DAO to find the managed entity within the transaction
        Role managedRole = roleDao.findById(role.getId());
        if (managedRole != null) {
            // Update the permissions set on the managed entity
            managedRole.getPermissions().clear();
            managedRole.getPermissions().addAll(newPermissions);
            // The transaction commit will handle the update
        }
    }
    public void createPermission(Permissions permission) {
        permissionDao.createPermission(permission);
    }

    public void deletePermission(Permissions permission) {
        permissionDao.deletePermission(permission);
    }
}