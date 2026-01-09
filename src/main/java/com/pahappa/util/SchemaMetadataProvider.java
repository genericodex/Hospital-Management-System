package com.pahappa.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class SchemaMetadataProvider {

    // List of entities available for reporting
    private static final List<Class<?>> ALLOWED_ENTITIES = Arrays.asList(
            com.pahappa.models.Patient.class,
            com.pahappa.models.Doctor.class,
            com.pahappa.models.Appointment.class,
            com.pahappa.models.Billing.class,
            com.pahappa.models.Staff.class
    );

    public List<String> getAvailableTables() {
        return ALLOWED_ENTITIES.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
    }

    public List<String> getFieldsForTable(String tableName) {
        Optional<Class<?>> entityClass = ALLOWED_ENTITIES.stream()
                .filter(c -> c.getSimpleName().equalsIgnoreCase(tableName))
                .findFirst();

        if (entityClass.isPresent()) {
            List<String> fieldNames = new ArrayList<>();
            Class<?> clazz = entityClass.get();

            // Get all fields (including those from parent classes like BaseEntity)
            List<Field> allFields = getAllFields(clazz);

            for (Field field : allFields) {
                // 1. Add direct simple fields (e.g., "status", "appointmentDate", "id")
                if (isSimpleType(field.getType())) {
                    fieldNames.add(field.getName());
                }

                // 2. Add relationship fields (e.g., "doctor.firstName", "patient.gender")
                // This enables the "Multi-Table" view you want
                if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                    addRelatedFields(fieldNames, field);
                }
            }
            return fieldNames;
        }
        return Collections.emptyList();
    }

    private void addRelatedFields(List<String> fieldNames, Field relationField) {
        Class<?> relatedClass = relationField.getType();
        String prefix = relationField.getName() + "."; // e.g., "doctor."

        // Get fields of the related entity
        List<Field> subFields = getAllFields(relatedClass);

        for (Field subField : subFields) {
            // Only add simple types to avoid infinite recursion
            if (isSimpleType(subField.getType())) {
                fieldNames.add(prefix + subField.getName()); // e.g., "doctor.firstName"
            }
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                String.class.isAssignableFrom(type) ||
                Temporal.class.isAssignableFrom(type) || // FIX: Supports LocalDateTime, LocalDate, etc.

                Number.class.isAssignableFrom(type) ||
                Date.class.isAssignableFrom(type) ||
                Boolean.class.isAssignableFrom(type) ||
                Enum.class.isAssignableFrom(type);
    }
}
