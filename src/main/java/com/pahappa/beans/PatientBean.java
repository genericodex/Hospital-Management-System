package com.pahappa.beans;

import com.pahappa.models.Patient;
import com.pahappa.services.PatientServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Serializable allows objects of the PatientBean class to be converted into a format
 * that can be easily saved to a file, sent over a network, or stored in memory, and then later restored
 * back into a real object.
 *
 * This is useful for web applications to keep user data between requests or sessions.
 */
@Named
@ViewScoped
public class PatientBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private PatientServiceImpl patientService;

    @Inject
    private AuthBean authBean;

    private List<Patient> patients;
    private List<Patient> selectedPatients;
    private List<Patient> deletedPatients;
    private Patient selectedPatient;
    private boolean newPatient;
    private Long patientToDeleteId;
    private String searchTerm;
    private List<Patient> filteredPatients;

    /**
     * The @PostConstruct annotation is used to indicate that this method should be called after the
     * constructor has been invoked and all dependency injections have been completed.
     *
     * in layman's terms, it means that this method will run automatically after the bean is created.
     *
     */
    @PostConstruct
    public void init() {
        patients = patientService.getAllActivePatient();
        selectedPatient = new Patient();
        // Always initialize filteredPatients to avoid null
        filteredPatients = new ArrayList<>(patients);
    }

    public void initNewPatient() {
        selectedPatient = new Patient();
        newPatient = true;
    }

    public void savePatient() {
        try {
            // Backend validation for phone number and birthdate
            if (!selectedPatient.isValidPhoneNumber()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Phone number must be exactly 10 digits."));
                return;
            }
            if (!selectedPatient.isValidBirthDate()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Birth date cannot be in the future."));
                return;
            }
            if (newPatient) {
                patientService.createPatient(
                        selectedPatient.getFirstName(),
                        selectedPatient.getLastName(),
                        selectedPatient.getDateOfBirth(),
                        selectedPatient.getContactNumber(),
                        selectedPatient.getAddress(),
                        selectedPatient.getEmail(),
                        false,
                        selectedPatient.getMedicalHistory(),
                        authBean != null ? authBean.getLoggedInUser() : null
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Patient created successfully"));
            } else {
                patientService.updatePatient(selectedPatient, authBean != null ? authBean.getLoggedInUser() : null);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Patient updated successfully"));
            }
            patients = patientService.getAllActivePatient();
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
            patientService.softDeletePatient(patient.getId(), authBean != null ? authBean.getLoggedInUser() : null);
            patients = patientService.getAllActivePatient();
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
                patientService.softDeletePatient(patient.getId(), authBean != null ? authBean.getLoggedInUser() : null);
            }
            patients = patientService.getAllActivePatient();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedPatients.size() + " patients deleted successfully"));
            selectedPatients = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadDeletedPatients() {
        deletedPatients = patientService.getDeletedPatient();
    }

    public void restorePatient(Long id) {
        try {
            patientService.restorePatient(id, authBean != null ? authBean.getLoggedInUser() : null);
            patients = patientService.getAllActivePatient();
            deletedPatients = patientService.getDeletedPatient();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Patient restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void hardDeletePatient(Long id) {
        try {
            patientService.deletePatient(id, authBean != null ? authBean.getLoggedInUser() : null);
            deletedPatients = patientService.getDeletedPatient();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Patient permanently deleted"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void editPatient(Patient patient) {
        // Fetch the managed Patient entity from DB to avoid detached entity issues
        this.selectedPatient = patientService.getPatientById(patient.getId());
        this.newPatient = false;
    }

    public boolean hasSelectedPatients() {
        return selectedPatients != null && !selectedPatients.isEmpty();
    }

    public void searchPatients() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            filteredPatients = new ArrayList<>(patients);
        } else {
            String lower = searchTerm.toLowerCase();
            filteredPatients = patients.stream()
                .filter(p -> (p.getFirstName() + " " + p.getLastName()).toLowerCase().contains(lower))
                .toList();
        }
    }

    // Getters and Setters
    public List<Patient> getPatients() { return patients; }
    public List<Patient> getSelectedPatients() { return selectedPatients; }
    public void setSelectedPatients(List<Patient> selectedPatients) { this.selectedPatients = selectedPatients; }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
    public List<Patient> getDeletedPatients() { return deletedPatients; }
    public boolean isNewPatient() { return newPatient; }
    public Long getPatientToDeleteId() { return patientToDeleteId; }
    public void setPatientToDeleteId(Long patientToDeleteId) { this.patientToDeleteId = patientToDeleteId; }
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public List<Patient> getFilteredPatients() {
        if (filteredPatients == null) {
            filteredPatients = new ArrayList<>(patients);
        }
        return filteredPatients;
    }
}
