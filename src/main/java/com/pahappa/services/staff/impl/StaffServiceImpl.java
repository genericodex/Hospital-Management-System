package com.pahappa.services.staff.impl;

import com.pahappa.dao.StaffDao;
import com.pahappa.models.*;
import java.util.Date;
import java.util.List;

import com.pahappa.services.HospitalService;
import com.pahappa.services.staff.StaffService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import com.pahappa.util.PasswordEncoder;

@Named
@ApplicationScoped
public class StaffServiceImpl implements StaffService {
    @Inject
    private StaffDao staffDao;

    @Inject
    private PasswordEncoder passwordEncoder;

    // Staff operations
    @Override
    public Staff createStaff(String firstName, String lastName, String email, String contact, Role role, String department, Date hireDate, String password, Boolean isDeleted, Staff performedBy) {
        String hashedPassword = passwordEncoder.encode(password);
        Staff staff = new Staff(firstName, lastName, email, contact, role, department, hireDate, hashedPassword, isDeleted);
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
        // 1. Find the staff member by their email address.
        Staff staff = staffDao.getStaffByEmail(email);

        // 2. If a staff member is found, check if the provided password matches the stored hash.
        if (staff != null && passwordEncoder.matches(password, staff.getPassword())) {
            return staff; // Login successful
        }
        // 3. If no user is found or the password doesn't match, return null.
        return null;

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

    @Override
    @Transactional
    public void changePassword(Long staffId, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Passwords cannot be empty.");
        }

        Staff staff = staffDao.getStaffById(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("User not found.");
        }

        // 1. Verify the current password is correct
        // Assuming you are using a password encoder like BCrypt
        if (!passwordEncoder.matches(oldPassword, staff.getPassword())) {
            throw new IllegalArgumentException("The current password you entered is incorrect.");
        }

        // 2. Hash the new password before saving
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        staff.setPassword(hashedNewPassword);

        // 3. Save the updated staff member
        staffDao.updateStaff(staff);
    }

    @Override
    public long countActiveStaff() {
        return staffDao.countActiveStaff();
    }
}
