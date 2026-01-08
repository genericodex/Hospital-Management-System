package com.pahappa.services.audit.impl;

import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import com.pahappa.models.AuditLogDetail;
import com.pahappa.models.Doctor; // Import Doctor
import com.pahappa.models.Role;
import com.pahappa.models.Staff;
import com.pahappa.services.audit.AuditService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Named
@ApplicationScoped
public class AuditServiceImpl implements AuditService {

    @Inject
    private AuditLogDao auditLogDao;

    @Override
    public void log(String actionType, String entityName, String entityId, Object oldState, Object newState, Object actor) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setTimestamp(LocalDateTime.now());

        // --- NEW: Handle different actor types ---
        if (actor instanceof Staff) {
            Staff staffActor = (Staff) actor;
            log.setStaffId(staffActor.getId());
            log.setStaffName(staffActor.getFirstName() + " " + staffActor.getLastName());
        } else if (actor instanceof Doctor) {
            Doctor doctorActor = (Doctor) actor;
            log.setStaffId(doctorActor.getId()); // Use the same field for simplicity
            log.setStaffName("Dr. " + doctorActor.getFirstName() + " " + doctorActor.getLastName());
        } else if (actor == null) {
            log.setStaffName("System");
        }
        // --- END OF NEW LOGIC ---

        // For CREATE actions, log all fields of the new state.
        if ("CREATE".equals(actionType) && newState != null) {
            // --- FIX: Get the real class, not the proxy ---
            Class<?> entityClass = getRealClass(newState);
            for (Field field : entityClass.getDeclaredFields()) {
                if (isFieldAuditable(field)) {
                    log.getDetails().add(createDetail(log, field, null, getFieldValue(field, newState)));
                }
            }
        }
        // For DELETE actions, log all fields of the old state.
        else if ("DELETE".equals(actionType) && oldState != null) {
            // --- FIX: Get the real class, not the proxy ---
            Class<?> entityClass = getRealClass(oldState);
            for (Field field : entityClass.getDeclaredFields()) {
                if (isFieldAuditable(field)) {
                    log.getDetails().add(createDetail(log, field, getFieldValue(field, oldState), null));
                }
            }
        }
        // For UPDATE actions, compare old and new states.
        else if ("UPDATE".equals(actionType) && oldState != null && newState != null) {
            // --- FIX: Get the real class, not the proxy ---
            Class<?> entityClass = getRealClass(newState);
            for (Field field : entityClass.getDeclaredFields()) {
                if (isFieldAuditable(field)) {
                    Object oldValue = getFieldValue(field, oldState);
                    Object newValue = getFieldValue(field, newState);
                    if (!Objects.equals(oldValue, newValue)) {
                        log.getDetails().add(createDetail(log, field, oldValue, newValue));
                    }
                }
            }
        }

        // Only save the log if there are details to save or for specific actions like LOGIN
        if (!log.getDetails().isEmpty() || "LOGIN".equals(actionType) || "LOGOUT".equals(actionType) || "PASSWORD_CHANGE".equals(actionType)) {
            auditLogDao.saveAuditLog(log);
        }
    }

    private AuditLogDetail createDetail(AuditLog parent, Field field, Object oldValue, Object newValue) {
        return new AuditLogDetail(
                parent,
                field.getName(),
                convertToString(oldValue),
                convertToString(newValue)
        );
    }

    private Object getFieldValue(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "ACCESS_ERROR";
        }
    }

    private String convertToString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Role) {
            return ((Role) value).getName();
        }
        return value.toString();
    }

    /**
     * Determines if a field should be included in the audit log details.
     * This prevents logging sensitive or unnecessary information.
     */
    private boolean isFieldAuditable(Field field) {
        // Don't log fields marked with @Transient, as they are not part of the persistent state.
        if (field.isAnnotationPresent(Transient.class)) {
            return false;
        }

        // Don't log passwords.
        if ("password".equalsIgnoreCase(field.getName())) {
            return false;
        }

        // Don't log collections (like a Patient's list of appointments) to avoid huge logs and potential errors.
        if (Collection.class.isAssignableFrom(field.getType())) {
            return false;
        }

        return true;
    }

    /**
     * Gets the actual entity class, bypassing any Hibernate proxy class.
     */
    private Class<?> getRealClass(Object proxy) {
        if (proxy.getClass().getName().contains("$$")) {
            return proxy.getClass().getSuperclass();
        }
        return proxy.getClass();
    }
}