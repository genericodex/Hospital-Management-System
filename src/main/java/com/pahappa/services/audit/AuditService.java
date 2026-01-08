package com.pahappa.services.audit;


/**
 * An interface defining the contract for the system's audit logging service.
 * This promotes loose coupling and allows for different implementations if needed.
 */
public interface AuditService {

    /**
     * Logs a system action, comparing the old and new states of an entity to record detailed changes.
     *
     * @param actionType The type of action (e.g., "CREATE", "UPDATE", "DELETE").
     * @param entityName The name of the entity being changed (e.g., "Patient", "Doctor").
     * @param entityId   The ID of the entity.
     * @param oldState   The object representing the state of the entity before the change. Can be null for CREATE actions.
     * @param newState   The object representing the state of the entity after the change. Can be null for DELETE actions.
     * @param actor      The user (can be Staff or Doctor) who performed the action. Can be null for system actions.
     */
    void log(String actionType, String entityName, String entityId, Object oldState, Object newState, Object actor);
}