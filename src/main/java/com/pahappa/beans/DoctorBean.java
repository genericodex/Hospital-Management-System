package com.pahappa.beans;

import com.pahappa.constants.Specialization;
import com.pahappa.models.Doctor;
import com.pahappa.services.HospitalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DoctorBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private HospitalService hospitalService;

    private List<Doctor> doctors;
    private List<Doctor> selectedDoctors;
    private List<Doctor> deletedDoctors;
    private Doctor selectedDoctor;
    private boolean newDoctor;

    @PostConstruct
    public void init() {
        doctors = hospitalService.getAllActiveDoctor();
        selectedDoctor = new Doctor();
    }

    public void initNewDoctor() {
        selectedDoctor = new Doctor();
        newDoctor = true;
    }

    public void saveDoctor() {
        try {
            // Server-side validation for required fields
            if (selectedDoctor.getFirstName() == null || selectedDoctor.getFirstName().trim().isEmpty() ||
                selectedDoctor.getLastName() == null || selectedDoctor.getLastName().trim().isEmpty() ||
                selectedDoctor.getSpecialization() == null ||
                selectedDoctor.getContactNumber() == null || selectedDoctor.getContactNumber().trim().isEmpty() ||
                selectedDoctor.getEmail() == null || selectedDoctor.getEmail().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "All fields are required."));
                return;
            }
            if (newDoctor) {
                hospitalService.createDoctor(
                        selectedDoctor.getFirstName(),
                        selectedDoctor.getLastName(),
                        selectedDoctor.getSpecialization(),
                        selectedDoctor.getContactNumber(),
                        selectedDoctor.getEmail(),
                        false
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Doctor created successfully"));
            } else {
                hospitalService.updateDoctor(selectedDoctor);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Doctor updated successfully"));
            }
            doctors = hospitalService.getAllActiveDoctor();
            // Reset selectedDoctor and newDoctor after save/update
            selectedDoctor = new Doctor();
            newDoctor = false;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteDoctor(Doctor doctor) {
        try {
            hospitalService.softDeleteDoctor(doctor.getId());
            doctors = hospitalService.getAllActiveDoctor();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Doctor deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteSelectedDoctors() {
        try {
            for (Doctor doctor : selectedDoctors) {
                hospitalService.softDeleteDoctor(doctor.getId());
            }
            doctors = hospitalService.getAllActiveDoctor();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedDoctors.size() + " doctors deleted successfully"));
            selectedDoctors = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadDeletedDoctors() {
        deletedDoctors = hospitalService.getDeletedDoctors();
    }

    public void restoreDoctor(Long id) {
        try {
            hospitalService.restoreDoctor(id);
            doctors = hospitalService.getAllActiveDoctor();
            deletedDoctors = hospitalService.getDeletedDoctors();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Doctor restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void editDoctor(Doctor doctor) {
        // Fetch the managed Doctor entity from DB to avoid detached entity issues
        this.selectedDoctor = hospitalService.getDoctorById(doctor.getId());
        this.newDoctor = false;
    }

    public Specialization[] getSpecializations() {
        return Specialization.values();
    }

    // Getters and Setters
    public List<Doctor> getDoctors() {
        if (doctors == null) {
            doctors = new java.util.ArrayList<>();
        }
        return doctors;
    }
    public List<Doctor> getSelectedDoctors() { return selectedDoctors; }
    public void setSelectedDoctors(List<Doctor> selectedDoctors) { this.selectedDoctors = selectedDoctors; }
    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(Doctor selectedDoctor) { this.selectedDoctor = selectedDoctor; }
    public List<Doctor> getDeletedDoctors() { return deletedDoctors; }
    public boolean isNewDoctor() { return newDoctor; }
}