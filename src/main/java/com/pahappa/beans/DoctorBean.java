package com.pahappa.beans;

import com.pahappa.constants.Specialization;
import com.pahappa.models.Doctor;
import com.pahappa.services.HospitalService;
import com.pahappa.services.doctor.DoctorService;
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
public class DoctorBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private transient DoctorService doctorService;

    @Inject
    private transient SettingsBean settingsBean;

    @Inject
    private transient AuthBean authBean; // Injected to get the current user's role

    private List<Doctor> doctors;
    private List<Doctor> selectedDoctors;
    private List<Doctor> deletedDoctors;
    private Doctor selectedDoctor;
    private boolean newDoctor;
    private Long doctorToDeleteId;
    private String searchTerm;
    private List<Doctor> filteredDoctors;
    private String password;

    @PostConstruct
    public void init() {
        try {
            // Load all necessary data ONCE and reliably.
            doctors = doctorService.getAllActiveDoctors();
            filteredDoctors = new ArrayList<>(doctors);
            deletedDoctors = new ArrayList<>();
            this.selectedDoctor = new Doctor();
        } catch (Exception e) {
            System.out.println("Failed to initialize DoctorBean");
            // Prevent page from crashing if DB is down
            doctors = new ArrayList<>();
            filteredDoctors = new ArrayList<>();
            deletedDoctors = new ArrayList<>();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not load doctor data."));
        }
    }

        // --- Helper methods for security ---
        private boolean isAdmin () {
            if (authBean != null && authBean.getStaff() != null && authBean.getStaff().getRole() != null) {
                return "ADMIN".equals(authBean.getStaff().getRole().getName()) || "IT SUPPORT".equals(authBean.getStaff().getRole().getName());
            }
            return false;
        }

        private void showAccessDeniedMessage () {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Access Denied", "You do not have permission to perform this action."));
        }

        // --- Action Methods ---

        public void initNewDoctor () {
            selectedDoctor = new Doctor();
            newDoctor = true;
            password = null;
        }

        public void saveDoctor () {
            if (!authBean.hasPermission("DOCTOR_CREATE") && !authBean.hasPermission("DOCTOR_EDIT")) {
                showAccessDeniedMessage();
                return;
            }
            try {
                if (newDoctor) {
                    Doctor savedDoctor = doctorService.createDoctor(
                            selectedDoctor.getFirstName(),
                            selectedDoctor.getLastName(),
                            selectedDoctor.getSpecialization(),
                            selectedDoctor.getContactNumber(),
                            selectedDoctor.getEmail(),
                            false,
                            password // Set password
                    );
                    selectedDoctor = savedDoctor; // Now has ID
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Doctor created successfully"));
                } else {
                    if (password != null && !password.isEmpty()) {
                        selectedDoctor.setPassword(password);
                    }
                    doctorService.updateDoctor(selectedDoctor, authBean.getStaff()); // Pass current user for audit log
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Doctor updated successfully"));
                }
                init();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }

        public void deleteDoctor (Doctor doctor){
            if (!isAdmin()) {
                showAccessDeniedMessage();
                return;
            }
            try {
                doctorService.softDeleteDoctor(doctor.getId(), authBean.getStaff()); // Pass current user for audit log
                doctors = doctorService.getAllActiveDoctors();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Doctor deleted successfully"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
            init();
        }

        public void deleteSelectedDoctors () {
            if (!isAdmin()) {
                showAccessDeniedMessage();
                return;
            }
            try {
                for (Doctor doctor : selectedDoctors) {
                    doctorService.softDeleteDoctor(doctor.getId(), authBean.getStaff()); // Pass current user for audit log
                }
                doctors = doctorService.getAllActiveDoctors();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(selectedDoctors.size() + " doctors deleted successfully"));
                selectedDoctors = null;
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
            init();
        }

        public void loadDeletedDoctors () {
            if (!isAdmin()) {
                showAccessDeniedMessage();
                deletedDoctors = new ArrayList<>();
                return;
            }
            deletedDoctors = doctorService.getDeletedDoctors();
        }

        public void restoreDoctor (Long id){
            if (!isAdmin()) {
                showAccessDeniedMessage();
                return;
            }
            try {
                doctorService.restoreDoctor(id, authBean.getStaff()); // Pass current user for audit log
                doctors = doctorService.getAllActiveDoctors();
                deletedDoctors = doctorService.getDeletedDoctors();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Doctor restored successfully"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
            init();
        }

        public void editDoctor (Doctor doctor){
            this.selectedDoctor = doctorService.getDoctorById(doctor.getId());
            this.newDoctor = false;
        }

        public Specialization[] getSpecializations () {
            return Specialization.values();
        }

        public List<String> getAvailableSpecializations () {
            return settingsBean.getSpecializations();
        }

        public void searchDoctors () {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                filteredDoctors = new ArrayList<>(doctors);
            } else {
                String lower = searchTerm.toLowerCase();
                filteredDoctors = doctors.stream()
                        .filter(d -> (d.getFirstName() + " " + d.getLastName()).toLowerCase().contains(lower)
                                || (d.getSpecialization() != null && d.getSpecialization().toLowerCase().contains(lower)))
                        .toList();
            }
        }

        // Getters and Setters
        public List<Doctor> getDoctors () {
            if (doctors == null || doctors.isEmpty()) {
                doctors = doctorService.getAllActiveDoctors();
            }
            return doctors;
        }
        public List<Doctor> getSelectedDoctors () {
            return selectedDoctors;
        }
        public void setSelectedDoctors (List < Doctor > selectedDoctors) {
            this.selectedDoctors = selectedDoctors;
        }
        public Doctor getSelectedDoctor () {
            return selectedDoctor;
        }
        public void setSelectedDoctor (Doctor selectedDoctor){
            this.selectedDoctor = selectedDoctor;
        }
        public List<Doctor> getDeletedDoctors () {
            if (deletedDoctors == null) {
                deletedDoctors = new ArrayList<>();
            }
            return deletedDoctors;
        }
        public String getPassword () {
            return password;
        }
        public void setPassword (String password){
            this.password = password;
        }
        public boolean isNewDoctor () {
            return newDoctor;
        }
        public Long getDoctorToDeleteId () {
            return doctorToDeleteId;
        }
        public void setDoctorToDeleteId (Long doctorToDeleteId){
            this.doctorToDeleteId = doctorToDeleteId;
        }
        public List<Doctor> getFilteredDoctors () {
            if (filteredDoctors == null) {
                filteredDoctors = new ArrayList<>(doctors);
            }
            return filteredDoctors;
        }
        public String getSearchTerm () {
            return searchTerm;
        }
        public void setSearchTerm (String searchTerm){
            this.searchTerm = searchTerm;
        }

        public void hardDeleteDoctor (Long id){
            if (!isAdmin()) {
                showAccessDeniedMessage();
                return;
            }
            try {
                doctorService.deleteDoctor(id, authBean.getStaff()); // Pass current user for audit log
                loadDeletedDoctors();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Doctor permanently deleted"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        }
    }

