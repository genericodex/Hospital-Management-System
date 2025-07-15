package com.pahappa.util;

import com.pahappa.dao.DoctorDao;
import com.pahappa.dao.StaffDao;
import com.pahappa.models.Doctor;
import com.pahappa.models.Staff;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A one-time utility to run on application startup.
 * Its purpose is to find any plain-text passwords remaining in the database
 * and securely hash them using the BCrypt algorithm.
 *
 * IMPORTANT: This class should be DELETED or COMMENTED OUT after you have
 * successfully run the application once and verified the passwords are hashed.
 */
@Singleton
@Startup
public class PasswordMigrationBean {

    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationBean.class);

    @Inject
    private StaffDao staffDao;

    @Inject
    private DoctorDao doctorDao;

    @Inject
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        logger.info("************************************************************");
        logger.info("PasswordMigrationBean: Starting one-time password migration check...");

        // This process needs to manage its own transaction as it runs outside the web request cycle.
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            migrateStaffPasswords(session);
            migrateDoctorPasswords(session);

            tx.commit();
            logger.info("Password migration check completed successfully.");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("CRITICAL ERROR during password migration.", e);
        }
        logger.info("IMPORTANT: If migration was successful, you should comment out or delete the PasswordMigrationBean class.");
        logger.info("************************************************************");
    }

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
                logger.warn("Migrated plain-text password for Staff: {}", staff.getEmail());
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
                logger.warn("Migrated plain-text password for Doctor: {}", doctor.getEmail());
            }
        }
        if (migratedCount > 0) {
            logger.info("Successfully migrated {} doctor passwords.", migratedCount);
        } else {
            logger.info("No plain-text doctor passwords found to migrate.");
        }
    }
}