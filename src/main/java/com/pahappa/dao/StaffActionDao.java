package com.pahappa.dao;

import com.pahappa.models.StaffAction;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class StaffActionDao {
    public void saveStaffAction(StaffAction action) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(action);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<StaffAction> getAllStaffActions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<StaffAction> query = session.createQuery("from StaffAction order by timestamp desc", StaffAction.class);
            return query.list();
        }
    }
}

