package com.pahappa.dao;

import com.pahappa.models.Role;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class RoleDao implements Serializable {

    public void createRole(Role role) {
        // The transaction is now managed by the OpenSessionInViewFilter or a @Transactional service.
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(role);
    }

    public Role findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.get(Role.class, id);
    }

    public List<Role> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery("from Role", Role.class).list();
    }

    // NEW METHOD: To prevent LazyInitializationException in the service
    public List<Role> findAllWithPermissions() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        // Use JOIN FETCH to load the permissions collection in the same query
        return session.createQuery("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions", Role.class).list();
    }

    public void updateRole(Role role) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.merge(role); // Use merge to save changes to a potentially detached object
    }

    public void deleteRole(Role role) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        // Ensure the role is managed before deleting
        Role managedRole = session.get(Role.class, role.getId());
        if (managedRole != null) {
            session.remove(managedRole);
        }
    }
}