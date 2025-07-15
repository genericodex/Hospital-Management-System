package com.pahappa.converters;

import com.pahappa.dao.RoleDao;
import com.pahappa.models.Role;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(forClass = Role.class, value = "roleConverter")
public class RoleConverter implements Converter {

    // Instantiating the DAO directly to match the project's existing pattern.
    private final RoleDao roleDao = new RoleDao();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            // The 'value' from the form is the Role ID. Fetch the Role object.
            return roleDao.findById(Integer.valueOf(value));
        } catch (NumberFormatException e) {
            // Handle cases where the value is not a valid integer
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Role) {
            // The 'value' is a Role object. Return its ID as a string.
            return String.valueOf(((Role) value).getId());
        }
        return "";
    }
}