package com.pahappa.dao;

import com.pahappa.models.AuditLog;
import com.pahappa.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

public class AuditLogDao implements Serializable {
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

    public List<AuditLog> getFilteredAuditLogs(String entityName, String actionType, String staffName, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        StringBuilder hql = new StringBuilder("FROM AuditLog WHERE 1=1");

        if (entityName != null && !entityName.trim().isEmpty()) {
            hql.append(" AND entityName LIKE :entityName");
        }
        if (actionType != null && !actionType.trim().isEmpty()) {
            hql.append(" AND actionType LIKE :actionType");
        }
        if (staffName != null && !staffName.trim().isEmpty()) {
            hql.append(" AND staffName LIKE :staffName");
        }
        if (startDate != null) {
            hql.append(" AND timestamp >= :startDate");
        }
        if (endDate != null) {
            hql.append(" AND timestamp <= :endDate");
        }

        hql.append(" ORDER BY timestamp DESC");

        Query<AuditLog> query = session.createQuery(hql.toString(), AuditLog.class);

        if (entityName != null && !entityName.trim().isEmpty()) {
            query.setParameter("entityName", "%" + entityName + "%");
        }
        if (actionType != null && !actionType.trim().isEmpty()) {
            query.setParameter("actionType", "%" + actionType + "%");
        }
        if (staffName != null && !staffName.trim().isEmpty()) {
            query.setParameter("staffName", "%" + staffName + "%");
        }
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.list();
    }
}

