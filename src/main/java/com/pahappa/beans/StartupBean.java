package com.pahappa.beans;

import com.pahappa.constants.StaffRoles;
import com.pahappa.models.Staff;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Named
@ApplicationScoped
public class StartupBean {
    public void ensureAdminUserExists(@Observes @Initialized(ApplicationScoped.class) Object init) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // Check if admin user exists by email (direct query)
            Staff adminUser = session.createQuery("FROM Staff WHERE email = :email", Staff.class)
                .setParameter("email", "admin@hospital.com")
                .uniqueResult();
            if (adminUser == null) {
                Staff admin = new Staff();
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@hospital.com");
                admin.setContactNumber("1234567890");
                admin.setRole(StaffRoles.ADMIN);
                admin.setDepartment("Administration");
                admin.setHireDate(new Date());
                admin.setPassword("admin123"); // You may want to hash this in production
                admin.setDeleted(false);
                session.save(admin);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
