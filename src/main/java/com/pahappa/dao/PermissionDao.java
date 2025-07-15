package com.pahappa.dao;

import com.pahappa.models.Permissions;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@ApplicationScoped
public class PermissionDao {

    public List<Permissions> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Permissions p ORDER BY p.permissionKey ", Permissions.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void createPermission(Permissions permission) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(permission);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Or log with a logger
        }
    }

    /**
     * Deletes a Permission record from the database.
     */
    public void deletePermission(Permissions permission) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(permission);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Or log with a logger
        }
    }
    // You can add create, update, delete methods here if you want to manage permissions dynamically
}