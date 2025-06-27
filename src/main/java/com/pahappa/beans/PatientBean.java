package com.pahappa.beans;

import com.pahappa.models.Patient;
import com.pahappa.services.HospitalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class PatientBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private HospitalService hospitalService;

    private List<Patient> patients;
    private List<Patient> selectedPatients;
    private List<Patient> deletedPatients;
    private Patient selectedPatient;
    private boolean newPatient;

    @PostConstruct
    public void init() {
        patients = hospitalService.getAllActivePatient();
        selectedPatient = new Patient();
    }

    public void initNewPatient() {
        selectedPatient = new Patient();
        newPatient = true;
    }

    public void savePatient() {
        try {
            if (newPatient) {
                hospitalService.createPatient(
                        selectedPatient.getFirstName(),
                        selectedPatient.getLastName(),
                        selectedPatient.getDateOfBirth(),
                        selectedPatient.getContactNumber(),
                        selectedPatient.getAddress(),
                        selectedPatient.getEmail(),
                        false
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Patient created successfully"));
            } else {
                hospitalService.updatePatient(selectedPatient);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Patient updated successfully"));
            }
            patients = hospitalService.getAllActivePatient();
            // Reset selectedPatient and newPatient after save/update
            selectedPatient = new Patient();
            newPatient = false;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deletePatient(Patient patient) {
        try {
            hospitalService.softDeletePatient(patient.getId());
            patients = hospitalService.getAllActivePatient();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Patient deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteSelectedPatients() {
        try {
            for (Patient patient : selectedPatients) {
                hospitalService.softDeletePatient(patient.getId());
            }
            patients = hospitalService.getAllActivePatient();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedPatients.size() + " patients deleted successfully"));
            selectedPatients = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadDeletedPatients() {
        deletedPatients = hospitalService.getDeletedPatient();
    }

    public void restorePatient(Long id) {
        try {
            hospitalService.restorePatient(id);
            patients = hospitalService.getAllActivePatient();
            deletedPatients = hospitalService.getDeletedPatient();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Patient restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void editPatient(Patient patient) {
        // Fetch the managed Patient entity from DB to avoid detached entity issues
        this.selectedPatient = hospitalService.getPatientById(patient.getId());
        this.newPatient = false;
    }

    public boolean hasSelectedPatients() {
        return selectedPatients != null && !selectedPatients.isEmpty();
    }

    // Getters and Setters
    public List<Patient> getPatients() { return patients; }
    public List<Patient> getSelectedPatients() { return selectedPatients; }
    public void setSelectedPatients(List<Patient> selectedPatients) { this.selectedPatients = selectedPatients; }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
    public List<Patient> getDeletedPatients() { return deletedPatients; }
    public boolean isNewPatient() { return newPatient; }
}