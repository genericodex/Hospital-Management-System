package com.pahappa.util;

import com.pahappa.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


/**
 * Imagine you're building a house. The Metadata and MetadataSources in Hibernate are like the architect's blueprint
 * and the materials used to create it.
                    MetadataSources = Your building materials (bricks, wood, nails)
 *                                      * These are the raw components you gather
 *                                      * In Hibernate: Entity classes, configuration files, database settings
 *                  Metadata = The completed blueprint
 *                                      * The organized plan showing how everything fits together
 *                                      * In Hibernate: The complete understanding of your database structure
 *                  SessionFactory = The constructed house. The final usable structure built from the blueprint
 * So here, the object that creates database sessions is in HibernateUtil.java

 *       1. Get building materials (MetadataSources)
 *      MetadataSources sources = new MetadataSources(registry);
 * <p>
 *       2. Add your entity classes (like adding bricks)
 *          sources.addAnnotatedClass(Patient.class);
 *          sources.addAnnotatedClass(Doctor.class);
 * <p>
 *      3. Create the blueprint (Metadata)
 *      Metadata metadata = sources.getMetadataBuilder().build();
 * <p>
 *      4. Build the house (SessionFactory)
 *      sessionFactory = metadata.getSessionFactoryBuilder().build();
 *          Key Concepts Explained Simply
 * 1. MetadataSources - The Raw Materials * What it is: A collection point for all your database mapping information
 *              What it contains: * Your entity classes (Patient.java, Doctor.java)
 *                                  * Configuration files (hibernate.cfg.xml)
 *                                  * Database connection settings
 *                                  * Analogy: Like gathering all your construction materials in one place before building
 * 2. Metadata - The Blueprint * What it is: The complete understanding of your database structure
 *                              * What it contains: How Java classes map to database tables,
 *                                                  Relationships between entities (e.g., Doctor has many Appointments)
 *                                                  * Column types and constraints
 *                                                  * Analogy: The architect's final plan showing room layouts, wiring, plumbing
 */
public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");

                // Register entity classes
                configuration.addAnnotatedClass(Patient.class);
                configuration.addAnnotatedClass(Doctor.class);
                configuration.addAnnotatedClass(Appointment.class);
                configuration.addAnnotatedClass(Billing.class);
                configuration.addAnnotatedClass(Staff.class);

                registry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                MetadataSources sources = new MetadataSources(registry);

                // Add entity classes to metadata sources
                sources.addAnnotatedClass(Patient.class);
                sources.addAnnotatedClass(Doctor.class);
                sources.addAnnotatedClass(Appointment.class);
                sources.addAnnotatedClass(Billing.class);
                sources.addAnnotatedClass(Staff.class);

                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}