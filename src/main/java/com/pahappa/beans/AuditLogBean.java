package com.pahappa.beans;

import com.pahappa.dao.AuditLogDao;
import com.pahappa.models.AuditLog;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AuditLogBean implements Serializable {
    private List<AuditLog> auditLogs;
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @PostConstruct
    public void init() {
        auditLogs = auditLogDao.getAllAuditLogs();
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }
}

