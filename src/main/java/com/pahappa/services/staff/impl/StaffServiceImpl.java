package com.pahappa.services.staff.impl;

import com.pahappa.dao.StaffDao;
import com.pahappa.models.Staff;
import com.pahappa.constants.StaffRoles;
import java.util.Date;
import java.util.List;

import com.pahappa.services.HospitalService;
import com.pahappa.services.staff.StaffService;
import jakarta.enterprise.context.ApplicationScoped;
import com.pahappa.models.Patient;
import com.pahappa.models.Doctor;
import com.pahappa.models.Appointment;
import com.pahappa.models.Billing;

@ApplicationScoped
public class StaffServiceImpl implements StaffService {
    private final StaffDao staffDao = new StaffDao();

    // Staff operations
    @Override
    public Staff createStaff(String firstName, String lastName, String email, String contact, StaffRoles role, String department, Date hireDate, String password, Boolean isDeleted, Staff performedBy) {
        Staff staff = new Staff(firstName, lastName, email, contact, role, department, hireDate, password, isDeleted);
        staffDao.saveStaff(staff);
        return staff;
    }

    @Override
    public List<Staff> getAllStaff() {
        return staffDao.getAllStaff();
    }

    @Override
    public Staff getStaffById(Long id) {
        return staffDao.getStaffById(id);
    }

    @Override
    public Staff authenticateStaff(String email, String password) {
        return staffDao.authenticate(email, password);
    }

    @Override
    public List<Staff> getAllActiveStaff() {
        return staffDao.getAllActiveStaff();
    }

    @Override
    public List<Staff> getDeletedStaff() {
        return staffDao.getDeletedStaff();
    }

    @Override
    public void updateStaff(Staff staff, Staff performedBy) {
        staffDao.updateStaff(staff);
    }

    @Override
    public void deleteStaff(Long id, Staff performedBy) {
        staffDao.deleteStaff(id);
    }

    @Override
    public void softDeleteStaff(Long id, Staff performedBy) {
        staffDao.softDeleteStaff(id);
    }

    @Override
    public void restoreStaff(Long id, Staff performedBy) {
        staffDao.restoreStaff(id);
    }

    @Override
    public Staff getStaffByEmail(String email) {
        return staffDao.getStaffByEmail(email);
    }
}
