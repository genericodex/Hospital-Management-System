package com.pahappa.dao;

import com.pahappa.models.AuditLog;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class AuditLogDao {
    public void saveAuditLog(AuditLog auditLog) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery("FROM AuditLog ORDER BY timestamp DESC", AuditLog.class).list();
    }

    public List<AuditLog> getAuditLogsByEntity(String entityName, String entityId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(
            "FROM AuditLog WHERE entityName = :entityName AND entityId = :entityId ORDER BY timestamp DESC",
            AuditLog.class)
            .setParameter("entityName", entityName)
            .setParameter("entityId", entityId)
            .list();
    }
}

