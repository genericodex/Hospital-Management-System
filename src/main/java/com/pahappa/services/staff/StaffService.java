package com.pahappa.services.staff;

import com.pahappa.models.Role;
import com.pahappa.models.Staff;

import java.util.Date;
import java.util.List;


public interface StaffService {
    Staff createStaff(String firstName, String lastName, String email, String contact, Role role, String department, Date hireDate, String password, Boolean isDeleted, Staff performedBy);
    List<Staff> getAllStaff();
    Staff getStaffById(Long id);
    Staff authenticateStaff(String email, String password);
    List<Staff> getAllActiveStaff();
    List<Staff> getDeletedStaff();
    void updateStaff(Staff staff, Staff performedBy);
    void deleteStaff(Long id, Staff performedBy);
    void softDeleteStaff(Long id, Staff performedBy);
    void restoreStaff(Long id, Staff performedBy);
    Staff getStaffByEmail(String mail);
    void changePassword(Long staffId, String oldPassword, String newPassword);
    long countActiveStaff();
}
