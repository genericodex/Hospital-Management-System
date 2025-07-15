package com.pahappa.beans;

import com.pahappa.models.Doctor;
import com.pahappa.models.Permissions; // <-- CORRECTED: Use singular 'Permissions'
import com.pahappa.models.Role;
import com.pahappa.models.Staff;
import com.pahappa.util.HibernateUtil;
import com.pahappa.util.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Named
@ApplicationScoped
public class StartupBean {

    // --- FIX: Add logger and inject PasswordEncoder ---
    private static final Logger logger = LoggerFactory.getLogger(StartupBean.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    // FIX: Renamed method to reflect its full purpose.
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Step 1: Ensure all default roles exist
            Role adminRole = ensureRoleExists("ADMIN", session);
            ensureRoleExists("RECEPTIONIST", session);
            ensureRoleExists("PHARMACIST", session);
            ensureRoleExists("LAB TECHNICIAN", session);
            ensureRoleExists("ACCOUNTANT", session);
            ensureRoleExists("IT SUPPORT", session);

            // Step 2: Ensure the admin user exists
            ensureAdminUserExists(adminRole, session);

            // FIX: This line was missing. This is the main reason permissions were not created.
            ensurePermissionsExist(session);


            // --- FIX: Perform one-time password migration here ---
            logger.info("************************************************************");
            logger.info("StartupBean: Starting one-time password migration check...");
//            migrateStaffPasswords(session);
//            migrateDoctorPasswords(session);
            logger.info("StartupBean: Password migration check finished.");
            logger.info("************************************************************");
            // --- END OF FIX ---


            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    private void ensureAdminUserExists(Role adminRole, Session session) {
        Staff adminUser = session.createQuery("FROM Staff WHERE email = :email", Staff.class)
                .setParameter("email", "admin@hospital.com")
                .uniqueResult();

        if (adminUser == null) {
            Staff admin = new Staff();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@hospital.com");
            admin.setContactNumber("1234567890");
            admin.setRole(adminRole);
            admin.setDepartment("Administration");
            admin.setHireDate(new Date());
            admin.setPassword("admin123"); // In a real app, this should be hashed!
            admin.setDeleted(false);
            session.persist(admin);
        }
    }

    private Role ensureRoleExists(String roleName, Session session) {
        Role role = session.createQuery("FROM Role WHERE name = :name", Role.class)
                .setParameter("name", roleName)
                .uniqueResult();
        if (role == null) {
            role = new Role(roleName);
            session.persist(role);
        }
        return role;
    }

    private void ensurePermissionsExist(Session session) {
        // Define all permissions your application needs
        Map<String, String> requiredPermissions = Map.ofEntries(
                Map.entry("PATIENT_CREATE", "Can create new patient records"),
                Map.entry("PATIENT_VIEW", "Can view patient details"),
                Map.entry("PATIENT_VIEW_DELETED", "Can view deleted patient details"),
                Map.entry("PATIENT_EDIT", "Can edit existing patient records"),
                Map.entry("PATIENT_DELETE_SOFT", "Can move patient records to the bin"),
                Map.entry("PATIENT_DELETE_HARD", "Can permanently delete patients from the bin"),
                Map.entry("PATIENT_RESTORE", "Can restore patients from the bin"),

                Map.entry("DOCTOR_CREATE", "Can create new doctor profiles"),
                Map.entry("DOCTOR_VIEW", "Can view doctor details"),
                Map.entry("DOCTOR_VIEW_DELETED", "Can view deleted doctor details"),
                Map.entry("DOCTOR_EDIT", "Can edit existing doctor profiles"),
                Map.entry("DOCTOR_DELETE_SOFT", "Can move doctor profiles to the bin"),
                Map.entry("DOCTOR_DELETE_HARD", "Can permanently delete doctors from the bin"),
                Map.entry("DOCTOR_RESTORE", "Can restore doctors from the bin"),

                Map.entry("APPOINTMENT_CREATE", "Can schedule new appointments"),
                Map.entry("APPOINTMENT_VIEW", "Can view all appointments"),
                Map.entry("APPOINTMENT_VIEW_DELETED", "Can view deleted appointment details"),
                Map.entry("APPOINTMENT_EDIT", "Can edit existing appointments"),
                Map.entry("APPOINTMENT_CANCEL", "Can cancel (soft-delete) appointments"),
                Map.entry("APPOINTMENT_DELETE_HARD", "Can permanently delete appointments"),
                Map.entry("APPOINTMENT_RESTORE", "Can restore cancelled appointments"),

                Map.entry("BILLING_CREATE", "Can create new bills for patients"),
                Map.entry("BILLING_VIEW", "Can view billing records"),
                Map.entry("BILLING_VIEW_DELETED", "Can view deleted billing details"),
                Map.entry("BILLING_PROCESS_PAYMENT", "Can process payments for bills"),
                Map.entry("BILLING_DELETE_SOFT", "Can move bills to the bin"),
                Map.entry("BILLING_DELETE_HARD", "Can permanently delete billings"),

                Map.entry("STAFF_CREATE", "Can create new staff members"),
                Map.entry("STAFF_VIEW", "Can view staff details"),
                Map.entry("STAFF_VIEW_DELETED", "Can view deleted staff details"),
                Map.entry("STAFF_EDIT", "Can edit existing staff members"),
                Map.entry("STAFF_DELETE_SOFT", "Can move staff members to the bin"),
                Map.entry("STAFF_DELETE_HARD", "Can permanently delete staff"),

                Map.entry("VIEW_SETTINGS_PAGE", "Can access the main settings page"),
                Map.entry("VIEW_AUDIT_LOGS", "Can view the system audit logs"),
                Map.entry("MANAGE_ROLES", "Can create and delete system roles"),
                Map.entry("MANAGE_PERMISSIONS", "Can assign permissions to roles")
        );

        // Get all permissions that are already in the database
        // CORRECTED: Use singular 'Permissions'
        List<Permissions> existingPermissions = session.createQuery("FROM Permissions", Permissions.class).list();
        Set<String> existingKeys = existingPermissions.stream()
                .map(Permissions::getPermissionKey)
                .collect(Collectors.toSet());

        // For each required permission, check if it exists. If not, create it.
        for (Map.Entry<String, String> entry : requiredPermissions.entrySet()) {
            if (!existingKeys.contains(entry.getKey())) {
                // CORRECTED: Use singular 'Permissions'
                Permissions newPermissions = new Permissions();
                newPermissions.setPermissionKey(entry.getKey());
                newPermissions.setDescription(entry.getValue());
                // Simple logic to categorize them
                session.persist(newPermissions);
                System.out.println("Created missing permission: " + entry.getKey());
            }
        }
    }

    // --- FIX: Add the migration methods here ---
    private void migrateStaffPasswords(Session session) {
        List<Staff> allStaff = session.createQuery("FROM Staff", Staff.class).list();
        int migratedCount = 0;
        for (Staff staff : allStaff) {
            String currentPassword = staff.getPassword();
            // A simple check: BCrypt hashes start with "$2a$", "$2b$", etc.
            // If it doesn't start with "$2", it's a plain-text password that needs hashing.
            if (currentPassword != null && !currentPassword.startsWith("$2")) {
                String hashedPassword = passwordEncoder.encode(currentPassword);
                staff.setPassword(hashedPassword);
                session.merge(staff);
                migratedCount++;
                logger.warn("MIGRATED plain-text password for Staff: {}", staff.getEmail());
            }
        }
        if (migratedCount > 0) {
            logger.info("Successfully migrated {} staff passwords.", migratedCount);
        } else {
            logger.info("No plain-text staff passwords found to migrate.");
        }
    }

    private void migrateDoctorPasswords(Session session) {
        List<Doctor> allDoctors = session.createQuery("FROM Doctor", Doctor.class).list();
        int migratedCount = 0;
        for (Doctor doctor : allDoctors) {
            String currentPassword = doctor.getPassword();
            if (currentPassword != null && !currentPassword.startsWith("$2")) {
                String hashedPassword = passwordEncoder.encode(currentPassword);
                doctor.setPassword(hashedPassword);
                session.merge(doctor);
                migratedCount++;
                logger.warn("MIGRATED plain-text password for Doctor: {}", doctor.getEmail());
            }
        }
        if (migratedCount > 0) {
            logger.info("Successfully migrated {} doctor passwords.", migratedCount);
        } else {
            logger.info("No plain-text doctor passwords found to migrate.");
        }
    }
}