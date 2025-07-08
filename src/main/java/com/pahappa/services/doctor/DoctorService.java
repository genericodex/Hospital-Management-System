package com.pahappa.services.doctor;

import com.pahappa.models.Doctor;
import com.pahappa.models.Staff;

import java.util.List;

public interface DoctorService {

    Doctor createDoctor(String firstName, String lastName, String specialization, String contactNumber, String email, Boolean isDeleted, String password);
    Doctor getDoctorById(Long id);
    List<Doctor> getAllDoctors();
    List<Doctor> getAllActiveDoctors();
    List<Doctor> getDeletedDoctors();
    void updateDoctor(Doctor doctor, Staff staff);
    void deleteDoctor(Long id, Staff staff);
    void softDeleteDoctor(Long id, Staff staff);
    void restoreDoctor(Long id, Staff staff);

}
