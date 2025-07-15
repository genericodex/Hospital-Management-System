package com.pahappa.dao;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class AppointmentDao {

    // Create
    public void saveAppointment(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getDoctor() == null) {
            throw new IllegalArgumentException("Patient and Doctor must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(appointment);
    }

    // Read
    public Appointment getAppointmentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        // FIX: Eagerly fetch related entities to prevent LazyInitializationException
        return session.createQuery(
                        "SELECT a FROM Appointment a " +
                                "LEFT JOIN FETCH a.patient " +
                                "LEFT JOIN FETCH a.doctor " +
                                "WHERE a.id = :id", Appointment.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    public List<Appointment> getAllAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery("FROM Appointment", Appointment.class).list();
    }

    // Update
    public void updateAppointment(Appointment appointment) {
        if (appointment.getId() == null) {
            throw new IllegalArgumentException("Cannot update an appointment without an ID.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment managedAppointment = session.get(Appointment.class, appointment.getId());
        if (managedAppointment != null){
            managedAppointment.setPatient(appointment.getPatient());
            managedAppointment.setDoctor(appointment.getDoctor());
            managedAppointment.setAppointmentTime(appointment.getAppointmentTime());
            managedAppointment.setStatus(appointment.getStatus());
            managedAppointment.setDeleted(appointment.isDeleted());
            managedAppointment.setNotes(appointment.getNotes());
        }
        session.merge(managedAppointment);
    }

    // Delete
    public void deleteAppointment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment appointment = session.get(Appointment.class, id);
        if (appointment != null) session.remove(appointment);
    }


    public void softDeleteAppointment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment appointment = session.get(Appointment.class, id);
        if (appointment != null && !appointment.isDeleted()) {
            appointment.setDeleted(true);
            session.merge(appointment);
        }
    }

    // Get all non-deleted appointment
    public List<Appointment> getAllActiveAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor WHERE a.isDeleted = false", Appointment.class
        ).list();
    }

    // Get all deleted appointment (bin)
    public List<Appointment> getDeletedAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM Appointment WHERE isDeleted = true", Appointment.class).list();
    }

    // Restore appointment
    public void restoreAppointment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appointment id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Appointment appointment = session.get(Appointment.class, id);
        if (appointment != null && appointment.isDeleted()) {
            appointment.setDeleted(false);
            session.merge(appointment);
        }
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        // Use try-with-resources and openSession for safe, read-only operations.
        Session session = HibernateUtil.getSessionFactory().openSession();
            // Use "LEFT JOIN FETCH" to prevent the N+1 query problem when accessing patient details later.
            return session.createQuery(
                            "FROM Appointment a LEFT JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND a.isDeleted = false",
                            Appointment.class)
                    .setParameter("doctorId", doctorId)
                    .list();

    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                        "FROM Appointment WHERE patient.id = :patientId AND isDeleted = false", Appointment.class)
                .setParameter("patientId", patientId)
                .list();
    }

    public List<Appointment> getFilteredAppointments(Long patientId, Long doctorId, LocalDateTime startDate, LocalDateTime endDate, String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start with a base query
            StringBuilder jpql = new StringBuilder("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor WHERE a.isDeleted = false");
            Map<String, Object> parameters = new HashMap<>();

            // Dynamically append conditions based on provided filters
            if (patientId != null) {
                jpql.append(" AND a.patient.id = :patientId");
                parameters.put("patientId", patientId);
            }
            if (doctorId != null) {
                jpql.append(" AND a.doctor.id = :doctorId");
                parameters.put("doctorId", doctorId);
            }
            if (startDate != null) {
                jpql.append(" AND a.appointmentTime >= :startDate");
                parameters.put("startDate", startDate);
            }
            if (endDate != null) {
                // To include the entire end day, we check for times *before* the start of the next day.
                jpql.append(" AND a.appointmentTime < :endDate");
                parameters.put("endDate", endDate.plusDays(1));
            }
            if (status != null && !status.isEmpty()) {
                jpql.append(" AND a.status = :status");
                parameters.put("status", com.pahappa.constants.AppointmentStatus.valueOf(status));
            }

            // Add ordering
            jpql.append(" ORDER BY a.appointmentTime ASC");

            TypedQuery<Appointment> query = session.createQuery(jpql.toString(), Appointment.class);

            // Set the parameters on the query
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        }
    }

    /**
     * NEW METHOD: Retrieves a map of appointment statuses and their counts for a specific doctor.
     * This is used to power the dashboard pie chart.
     * @param doctorId The ID of the doctor.
     * @return A Map where the key is the status (e.g., "SCHEDULED") and the value is the count.
     */
    public Map<String, Long> getAppointmentStatusCountsByDoctor(Long doctorId) {
        Map<String, Long> statusCounts = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> results = session.createQuery(
                            "SELECT a.status, COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.isDeleted = false GROUP BY a.status", Object[].class)
                    .setParameter("doctorId", doctorId)
                    .list();

            for (Object[] result : results) {
                if (result[0] instanceof AppointmentStatus) {
                    String status = ((AppointmentStatus) result[0]).name();
                    Long count = (Long) result[1];
                    statusCounts.put(status, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty map in case of an error
        }
        return statusCounts;
    }

    /**
     * NEW METHOD: Gets all deleted (cancelled) appointments for a specific doctor.
     * This will be used to filter the "View Cancelled" dialog for doctors.
     */
    public List<Appointment> getDeletedAppointmentsByDoctor(Long doctorId) {
        if (doctorId == null) {
            return Collections.emptyList();
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                        "FROM Appointment a LEFT JOIN FETCH a.patient WHERE a.isDeleted = true AND a.doctor.id = :doctorId",
                        Appointment.class)
                .setParameter("doctorId", doctorId)
                .list();
    }

    /**
     * FIX: Efficiently fetches a limited number of the most recent appointments.
     * This replaces loading all appointments into memory.
     * @param limit The maximum number of appointments to return.
     * @return A list of recent appointments.
     */
    public List<Appointment> findRecentAppointments(int limit) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                        "SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor " +
                                "WHERE a.isDeleted = false ORDER BY a.appointmentTime DESC", Appointment.class)
                .setMaxResults(limit)
                .list();
    }


    /**
     * NEW METHOD: Efficiently gets the count of appointments per day for a given date range.
     * This is perfect for a bar chart.
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A Map where the key is the date and the value is the count of appointments.
     */
    public Map<LocalDate, Long> getDailyAppointmentCounts(LocalDate startDate, LocalDate endDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Object[]> results = session.createQuery(
                        "SELECT function('date', a.appointmentTime), COUNT(a.id) " +
                                "FROM Appointment a " +
                                "WHERE a.isDeleted = false AND function('date', a.appointmentTime) BETWEEN :startDate AND :endDate " +
                                "GROUP BY function('date', a.appointmentTime) " +
                                "ORDER BY function('date', a.appointmentTime)", Object[].class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        // Convert the raw results into a clean Map for the bean
        return results.stream().collect(
                Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(), // HQL date function often returns java.sql.Date
                        row -> (Long) row[1]
                )
        );
    }
    /**
     * NEW METHOD: Gets a map of all appointment statuses and their counts.
            * This is a variation of your getAppointmentStatusCountsByDoctor method.
            * @return A Map where the key is the status (e.g., "SCHEDULED") and the value is the count.
            */
    public Map<AppointmentStatus, Long> getGlobalAppointmentStatusCounts() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Object[]> results = session.createQuery(
                        "SELECT a.status, COUNT(a.id) FROM Appointment a WHERE a.isDeleted = false GROUP BY a.status", Object[].class)
                .list();

        return results.stream().collect(
                Collectors.toMap(
                        row -> (AppointmentStatus) row[0],
                        row -> (Long) row[1]
                )
        );
    }

    public long countActiveAppointments() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String hql = "SELECT count(a.id) FROM Appointment a WHERE a.isDeleted = false";
        Long count = session.createQuery(hql, Long.class).uniqueResult();
        return count != null ? count : 0L; // Return 0 if the result is null
    }

    /**
     * NEW, EFFICIENT CHART METHOD: Gets the number of appointments per doctor.
     * This is used for the Doctor Workload bar chart on the main dashboard.
     * @return A List of Object arrays, where each array contains [firstName, lastName, count].
     */
    public List<Object[]> getAppointmentCountsByDoctor() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String hql = "SELECT d.firstName, d.lastName, COUNT(a.id) " +
                "FROM Appointment a JOIN a.doctor d " +
                "WHERE a.isDeleted = false " +
                "GROUP BY d.id, d.firstName, d.lastName " +
                "ORDER BY COUNT(a.id) DESC";

        // You might want to add a .setMaxResults(10) here if you only want to see the top 10 doctors
        return session.createQuery(hql, Object[].class).list();
    }

    public List<Appointment> getTodaysAppointmentsForDoctor(Long doctorId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        // This HQL compares the date part of appointmentTime with the current date.
        // Note: The exact function for date extraction might vary slightly between databases (e.g., DATE() for MySQL/PostgreSQL, CAST(.. AS DATE) for others).
        // This version using FUNCTION is generally portable with Hibernate.
        String hql = "FROM Appointment a " +
                "WHERE a.doctor.id = :doctorId " +
                "AND a.status = 'SCHEDULED' " +
                "AND FUNCTION('DATE', a.appointmentTime) = CURRENT_DATE " +
                "ORDER BY a.appointmentTime ASC";

        return session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId)
                .list();
    }

    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        // Define the date range: from the start of today to the start of the day after tomorrow.
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfTomorrow = LocalDate.now().plusDays(2).atStartOfDay();

        String hql = "FROM Appointment a " +
                "WHERE a.doctor.id = :doctorId " +
                "AND a.status = 'SCHEDULED' " +
                "AND a.appointmentTime >= :startRange " +
                "AND a.appointmentTime < :endRange " +
                "ORDER BY a.appointmentTime ASC";

        return session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId)
                .setParameter("startRange", startOfToday)
                .setParameter("endRange", endOfTomorrow)
                .list();
    }

    /**
     * NEW: Finds a single appointment by its primary key (ID).
     * @param id The ID of the appointment to find.
     * @return The Appointment object, or null if not found.
     */
    public Appointment findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL with "JOIN FETCH" is the standard way to solve this problem.
            // It tells Hibernate to load the related entities in the same query.
            String hql = "FROM Appointment a " +
                    "JOIN FETCH a.patient " +
                    "JOIN FETCH a.doctor " +
                    "WHERE a.id = :id";
            return session.createQuery(hql, Appointment.class)
                    .setParameter("id", id)
                    .uniqueResultOptional() // A modern, safe way to get a single result
                    .orElse(null);
        }

    }

    /**
     * NEW: Updates an existing appointment record in the database.
     * @param appointment The appointment object with updated values.
     */
    public void updateCalendarAppointment(Appointment appointment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(appointment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Or log with a logger
        }
    }

}

