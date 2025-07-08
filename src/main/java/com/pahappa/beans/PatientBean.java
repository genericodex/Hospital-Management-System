package com.pahappa.beans;

import com.pahappa.models.Patient;
import com.pahappa.services.patient.impl.PatientServiceImpl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Serializable allows objects of the PatientBean class to be converted into a format
 * that can be easily saved to a file, sent over a network, or stored in memory, and then later restored
 * back into a real object.
 *
 * This is useful for web applications to keep user data between requests or sessions.
 *
 * <p>
 * long -- primitive type in Java.<p>
 * Long -- an object type (a class that wraps a long value).
 *
 * Java expects serialVersionUID to be a primitive long (not a Long object) because:<p>
 * The Serializable interface and the Java serialization mechanism are designed to use a simple, fixed-size value for versioning classes.<p>
 * A primitive long is more efficient: it uses less memory and is faster to read/write than a Long object, which is a wrapper class and can be null.<p>
 * The serialization process is low-level and expects a constant value, not an object reference that could be null or have extra object overhead.<p>
 * The Java compiler and serialization runtime specifically look for a static final long field named serialVersionUID. If you use Long,
 * it will not be recognized and will not work as intended.<p><p>
 * In summary: serialVersionUID must be a primitive long so that Java serialization can reliably and efficiently check class versions.
 */
@Named
@ViewScoped
public class PatientBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PatientBean.class);
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
        logger.debug("PatientBean @PostConstruct init called");
        patients = patientService.getAllActivePatient();
        logger.debug("Loaded patients: {}", patients != null ? patients.size() : 0);
        selectedPatient = new Patient();
        // Always initialize filteredPatients to avoid null
        filteredPatients = new ArrayList<>(patients);
    }

    public void initNewPatient() {
        selectedPatient = new Patient();
        newPatient = true;
    }

    public void savePatient() {
        logger.debug("savePatient called. newPatient: {}, selectedPatient: {}", newPatient, selectedPatient);
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
            logger.info("Patient saved/updated successfully");
            // Reset selectedPatient and newPatient after save/update
            selectedPatient = new Patient();
            newPatient = false;
        } catch (Exception e) {
            logger.error("Error in savePatient", e);
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
        }
        // 1. Create a new empty list to hold the results
        List<Patient> results = new ArrayList<>();
        String lowerCaseSearchTerm = searchTerm.toLowerCase();

        // 2. Loop through each patient in the original list
        for (Patient patient : patients) {
            // Defensive check in case first or last name could be null
            String firstName = patient.getFirstName() != null ? patient.getFirstName() : "";
            String lastName = patient.getLastName() != null ? patient.getLastName() : "";
            String fullName = (firstName + " " + lastName).toLowerCase();

            // 3. If the patient's name matches, add them to the results list
            if (fullName.contains(lowerCaseSearchTerm)) {
                results.add(patient);
            }
        }

        // 4. Assign the new list of results to filteredPatients
        filteredPatients = results;
    }

    // Getters and Setters
    public List<Patient> getPatients() {
        logger.debug("getPatients called. patients size: {}", patients != null ? patients.size() : 0);
        if (patients == null || patients.isEmpty()) {
            patients = patientService.getAllActivePatient();
            logger.debug("Reloaded patients from DB. New size: {}", patients != null ? patients.size() : 0);
        }
        return patients;
    }
    public List<Patient> getSelectedPatients() { return selectedPatients; }
    public void setSelectedPatients(List<Patient> selectedPatients) { this.selectedPatients = selectedPatients; }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
    public List<Patient> getDeletedPatients() {
        if (deletedPatients == null) {
            deletedPatients = patientService.getDeletedPatient();
        }
        return deletedPatients;
    }
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
