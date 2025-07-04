package com.pahappa.services;

import com.pahappa.dao.StaffActionDao;
import com.pahappa.models.StaffAction;

import java.util.List;

public class StaffActionService {
    private final StaffActionDao staffActionDao = new StaffActionDao();

    public void logAction(StaffAction action) {
        staffActionDao.saveStaffAction(action);
    }

    public List<StaffAction> getAllActions() {
        return staffActionDao.getAllStaffActions();
    }
}

