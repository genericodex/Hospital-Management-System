package com.pahappa.dao;

import com.pahappa.models.Staff;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import java.util.List;
@ApplicationScoped //
public class StaffDao {

    // Create
    public void saveStaff(Staff staff) {
        // Null checks for required fields
        if (staff.getFirstName() == null || staff.getLastName() == null || staff.getEmail() == null || staff.getContactNumber() == null || staff.getRole() == null || staff.getDepartment() == null || staff.getHireDate() == null || staff.getPassword() == null) {
            throw new IllegalArgumentException("All staff fields must be provided and not null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Staff newStaff = new Staff();
        newStaff.setFirstName(staff.getFirstName());
        newStaff.setLastName(staff.getLastName());
        newStaff.setEmail(staff.getEmail());
        newStaff.setContactNumber(staff.getContactNumber());
        newStaff.setRole(staff.getRole());
        newStaff.setDepartment(staff.getDepartment());
        newStaff.setHireDate(staff.getHireDate());
        newStaff.setPassword(staff.getPassword());
        newStaff.setDeleted(staff.isDeleted());
        session.persist(newStaff);
    }

    // Read
    public Staff getStaffById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Staff id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.get(Staff.class, id);
    }

    public List<Staff> getAllStaff() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
                "FROM com.pahappa.models.Staff", Staff.class).list();
    }

    // Update
    public void updateStaff(Staff staff) {
        if (staff.getId() == null || staff.getFirstName() == null || staff.getLastName() == null || staff.getEmail() == null || staff.getContactNumber() == null || staff.getRole() == null || staff.getDepartment() == null || staff.getHireDate() == null || staff.getPassword() == null) {
            throw new IllegalArgumentException("All staff fields and ID must be provided and not null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Staff managedStaff = session.get(Staff.class, staff.getId());
        if (managedStaff != null) {
            managedStaff.setFirstName(staff.getFirstName());
            managedStaff.setLastName(staff.getLastName());
            managedStaff.setEmail(staff.getEmail());
            managedStaff.setContactNumber(staff.getContactNumber());
            managedStaff.setRole(staff.getRole());
            managedStaff.setDepartment(staff.getDepartment());
            managedStaff.setHireDate(staff.getHireDate());
            managedStaff.setPassword(staff.getPassword());
            managedStaff.setDeleted(staff.isDeleted());
            session.merge(managedStaff);
        }
    }

    // Delete
    public void deleteStaff(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Staff id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Staff staff = session.get(Staff.class, id);
        if (staff != null) session.remove(staff);
    }

    public Staff authenticate(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM com.pahappa.models.Staff WHERE email = :email AND password = :password AND isDeleted = false", Staff.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void softDeleteStaff(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Staff id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Staff staff = session.get(Staff.class, id);
        if (staff != null && !staff.isDeleted()) {
            staff.setDeleted(true);
            session.merge(staff);
        }
    }

    // Get all non-deleted staff
    public List<Staff> getAllActiveStaff() {
        Session session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "FROM com.pahappa.models.Staff WHERE isDeleted = false", Staff.class).list();
    }

    // Get all deleted staff (bin)
    public List<Staff> getDeletedStaff() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM com.pahappa.models.Staff WHERE isDeleted = true", Staff.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Restore staff
    public void restoreStaff(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Staff id must not be null.");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Staff staff = session.get(Staff.class, id);
        if (staff != null && staff.isDeleted()) {
            staff.setDeleted(false);
            session.merge(staff);
        }
    }

    public long countActiveStaff() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String hql = "SELECT count(s.id) FROM Staff s WHERE s.isDeleted = false";
        Long count = session.createQuery(hql, Long.class).uniqueResult();
        return count != null ? count : 0L;
    }

    public Staff getStaffByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty.");
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM com.pahappa.models.Staff WHERE email = :email AND isDeleted = false", Staff.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}