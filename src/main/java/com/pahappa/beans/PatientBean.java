package com.pahappa.beans;

import com.pahappa.models.Patient;
import com.pahappa.services.patient.PatientService;
import com.pahappa.services.patient.impl.PatientServiceImpl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ViewScoped: This is a crucial lifecycle annotation. It means one instance of
 * PatientBean is created when I first visit the patients.xhtml page, and it
 * LIVES as long as I stay on that page (e.g., sorting, filtering). If I
 * navigate to the dashboard and come back, a BRAND NEW PatientBean is created.
 * This is perfect for managing a single page's state.
 */
@Named
@ViewScoped
public class PatientBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PatientBean.class);
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * @Inject: This is powerful in implementing Dependency Injection. Instead of writing
     * `patientService = new PatientServiceImpl()`, you are asking the Jakarta EE
     * container to "inject" or provide the correct, managed instance of your
     * service. This is excellent practice.
     * <p>
     * {@code transient} keyword tells Java not to save this field if the bean
     * is ever serialized. This is because the service is a connection
     * to the backend, not part of the bean's data.
     */
    @Inject
    private transient PatientService patientService;

    @Inject
    private transient AuthBean authBean;

    private List<Patient> patients;
    private List<Patient> selectedPatients;
    private List<Patient> deletedPatients;
    private Patient selectedPatient;
    private boolean newPatient;
    private Long patientToDeleteId;
    private String searchTerm;
    private List<Patient> filteredPatients;
    /**
     * @PostConstruct: This annotation marks a method as the "setup routine".
     * This 'init' method is automatically called by the container immediately
     * after the PatientBean object is created, but BEFORE the user sees the page.
     * It's the perfect place to load initial data.
     */
    @PostConstruct
    public void init() {
        logger.debug("PatientBean @PostConstruct init called");
        patients = patientService.getAllActivePatient();
        logger.debug("Loaded patients: {}", patients != null ? patients.size() : 0);
        selectedPatient = new Patient();
        filteredPatients = new ArrayList<>(patients);
    }


    private void showAccessDeniedMessage() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Access Denied", "You do not have permission to perform this action."));
    }

    // --- Action Methods ---

    public void initNewPatient() {
        selectedPatient = new Patient();
        newPatient = true;
    }

    public void savePatient() {
        if (newPatient && !authBean.hasPermission("PATIENT_CREATE")) {
            showAccessDeniedMessage();
            return;
        }
        if (!newPatient && !authBean.hasPermission("PATIENT_EDIT")) {
            showAccessDeniedMessage();
            return;
        }
        logger.debug("savePatient called. newPatient: {}, selectedPatient: {}", newPatient, selectedPatient);
        try {
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
                        authBean.getStaff()
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Patient created successfully"));
            } else {
                patientService.updatePatient(selectedPatient, authBean.getStaff());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Patient updated successfully"));
            }
            init();
            logger.info("Patient saved/updated successfully");

        } catch (Exception e) {
            logger.error("Error in savePatient", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deletePatient(Patient patient) {
        if (!authBean.hasPermission("PATIENT_DELETE_SOFT")) {
            showAccessDeniedMessage();
            return;
        }
        try {
            patientService.softDeletePatient(patient.getId(), authBean.getStaff());
            patients = patientService.getAllActivePatient();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Patient deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deleteSelectedPatients() {
        if (!authBean.hasPermission("PATIENT_DELETE_SOFT")) {
            showAccessDeniedMessage();
            return;
        }
        try {
            for (Patient patient : selectedPatients) {
                patientService.softDeletePatient(patient.getId(), authBean.getStaff());
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
        if (!authBean.hasPermission("PATIENT_VIEW_DELETED") ) {
            showAccessDeniedMessage();
            deletedPatients = new ArrayList<>();
            return;
        }
        deletedPatients = patientService.getDeletedPatient();
    }

    public void restorePatient(Long id) {
        if (!authBean.hasPermission("PATIENT_RESTORE")) {
            showAccessDeniedMessage();
            return;
        }
        try {
            patientService.restorePatient(id, authBean.getStaff());
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
        if (!authBean.hasPermission("PATIENT_DELETE_HARD")) {
            showAccessDeniedMessage();
            return;
        }
        try {
            patientService.deletePatient(id, authBean.getStaff());
            deletedPatients = patientService.getDeletedPatient();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Patient permanently deleted"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

        /**
         * Go to the database (via the service) and fetch a fresh, complete copy of the
         *  patient's record using the ID from the object that was clicked on the page.
         * <p>
         *  This is a critical step to ensure we are editing the most up-to-date data,
         *  and it places this fresh record into the {@code selectedPatient} field, which is
         *  what the pop-up edit dialog is bound to.
         */
    public void editPatient(Patient patient) {
        this.selectedPatient = patientService.getPatientById(patient.getId());
        this.newPatient = false;
    }
        /**
         * When this method is called, the {@code this} keyword refers to the specific PatientBean object (the "clerk on duty") that received the user's click.
         * This line modifies the state of THAT specific object's 'selectedPatient' field.
         *
         */

    public boolean hasSelectedPatients() {
        return selectedPatients != null && !selectedPatients.isEmpty();
    }

    public void searchPatients() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            filteredPatients = new ArrayList<>(patients);
            return;
        }
        List<Patient> results = new ArrayList<>();
        String lowerCaseSearchTerm = searchTerm.toLowerCase();

        for (Patient patient : patients) {
            String firstName = patient.getFirstName() != null ? patient.getFirstName() : "";
            String lastName = patient.getLastName() != null ? patient.getLastName() : "";
            String fullName = (firstName + " " + lastName).toLowerCase();

            if (fullName.contains(lowerCaseSearchTerm)) {
                results.add(patient);
            }
        }
        filteredPatients = results;
    }

    // --- Getters and Setters ---
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
            deletedPatients = new ArrayList<>();
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
