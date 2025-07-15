package com.pahappa.converters;

import com.pahappa.beans.SettingsBean;
import com.pahappa.models.Permissions;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "permissionConverter", managed = true)
public class PermissionConverter implements Converter<Permissions> {

    @Inject
    private SettingsBean settingsBean; // Inject the bean that holds the list of all permissions

    @Override
    public Permissions getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Find the permission object from the full list in the settings bean
        return settingsBean.getPermissionsModel().getSource().stream()
                .filter(p -> p.getId().equals(Long.valueOf(value)))
                .findFirst()
                .orElseGet(() -> settingsBean.getPermissionsModel().getTarget().stream()
                        .filter(p -> p.getId().equals(Long.valueOf(value)))
                        .findFirst().orElse(null));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Permissions value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}