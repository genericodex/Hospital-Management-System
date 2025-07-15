package com.pahappa.beans;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;
import com.pahappa.services.appointment.AppointmentService;
import com.pahappa.services.appointment.impl.AppointmentServiceImpl;
import com.pahappa.services.doctor.DoctorService;
import com.pahappa.services.patient.PatientService;
import com.pahappa.services.patient.impl.PatientServiceImpl;
import com.pahappa.services.doctor.impl.DoctorServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AppointmentBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private transient AppointmentService appointmentService;

    @Inject
    private transient DoctorAuthBean doctorAuthBean;

    @Inject
    private transient PatientService patientService;

    @Inject
    private transient DoctorService doctorService;

    @Inject
    private transient AuthBean authBean; // Injected to get the current user's role

    private List<Appointment> appointments = new ArrayList<>();
    private List<Appointment> selectedAppointments;
    private List<Appointment> cancelledAppointments;
    private Appointment selectedAppointment;
    private List<Patient> patients;
    private List<Doctor> doctors;

    private Long selectedPatientId;
    private Long selectedDoctorId;

    private String selectedPatientName;
    private String selectedDoctorName;

    private Patient selectedPatient;
    private Doctor selectedDoctor;

    private Long appointmentToDeleteId;

    private Patient filterPatient;
    private Doctor filterDoctor;
    private LocalDateTime filterStartDate;
    private LocalDateTime filterEndDate;
    private String filterStatus;
    private boolean newAppointment;

    @PostConstruct
    public void init() {
        try {

            selectedAppointment = new Appointment();
            // Load lists for dropdowns/autocomplete fields ONCE.
            patients = patientService.getAllActivePatient();
            doctors = doctorService.getAllActiveDoctors();


            // --- THIS IS THE FIX ---
            // If a doctor is logged in (from DoctorAuthBean), automatically set them
            // as the filter criteria before loading the appointment list.
            if (doctorAuthBean != null && doctorAuthBean.getLoggedInDoctor() != null) {
                this.filterDoctor = doctorAuthBean.getLoggedInDoctor();
            }
            // --- END OF FIX ---

            // Load the main list of appointments using the filter method.
            // On first load, filters are null, so it gets all active appointments.
            filterAppointments();

        } catch (Exception e) {
            System.out.println("Error during AppointmentBean initialization");
            // Initialize with empty lists to prevent NullPointerExceptions on the page
            appointments = new ArrayList<>();
            patients = new ArrayList<>();
            doctors = new ArrayList<>();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", "Could not load appointment data."));
        }
    }

    // --- Helper methods for security ---
    private boolean isAdmin() {
        if (authBean != null && authBean.getStaff() != null && authBean.getStaff().getRole() != null) {
            return "ADMIN".equals(authBean.getStaff().getRole().getName())|| "IT SUPPORT".equals(authBean.getStaff().getRole().getName());
        }
        if (doctorAuthBean != null && doctorAuthBean.getLoggedInDoctor() != null) {
                return true;
                 }

        return false;
    }

    private void showAccessDeniedMessage() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Access Denied", "You do not have permission to perform this action."));
    }

    // --- Action Methods ---

    public void initNewAppointment() {
        selectedAppointment = new Appointment();
        this.selectedPatientId = null;
        this.selectedDoctorId = null;
        this.newAppointment = true;
        // If doctor is logged in, set selectedDoctor to logged-in doctor
        if (doctorAuthBean != null && doctorAuthBean.getLoggedInDoctor() != null) {
            this.selectedDoctor = doctorAuthBean.getLoggedInDoctor();
            this.selectedDoctorId = this.selectedDoctor.getId();
        } else {
            this.selectedDoctor = null;
        }
    }

    public void saveAppointment() {
        if (!authBean.hasPermission("APPOINTMENT_EDIT") && doctorAuthBean.getLoggedInDoctor() == null) {
            showAccessDeniedMessage();
            return;
        }
        try {
            // The required="true" on the JSF components handles most null checks.
            // We just need to check for logical errors.
            if (!selectedAppointment.isValidAppointmentTime()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Appointment date/time cannot be in the past."));
                return; // Stop processing
            }

            // Assign the objects from the autocomplete components to the appointment
            selectedAppointment.setPatient(selectedPatient);
            selectedAppointment.setDoctor(selectedDoctor);

            String message;
            if (selectedAppointment.getId() == null && newAppointment) {
                appointmentService.createAppointment(selectedAppointment);
                message = "Appointment created successfully.";
            } else {
                appointmentService.updateAppointment(selectedAppointment);
                message = "Appointment updated successfully.";
            }

            // The single most reliable way to refresh the page state
            init();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));

        } catch (Exception e) {
            // Log the exception for debugging
            // log.error("Error saving appointment", e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An unexpected error occurred."));
        }
    }

    public void cancelAppointment(Appointment appointment) {
        if (!authBean.hasPermission("APPOINTMENT_EDIT") && doctorAuthBean.getLoggedInDoctor() == null) {
            showAccessDeniedMessage();
            return;
        }
        try {
            appointmentService.cancelAppointment(appointment.getId());
            appointments = appointmentService.getAllActiveAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment cancelled successfully"));
            filterAppointments();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void cancelSelectedAppointments() {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            for (Appointment appointment : selectedAppointments) {
                appointmentService.cancelAppointment(appointment.getId());
            }
            appointments = appointmentService.getAllActiveAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedAppointments.size() + " appointments cancelled successfully"));
            selectedAppointments = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void completeAppointment(Appointment appointment) {
        if (!authBean.hasPermission("APPOINTMENT_EDIT") && doctorAuthBean.getLoggedInDoctor() == null) {
            showAccessDeniedMessage();
            return;
        }
        try {
            Appointment managedAppointment = appointmentService.getAppointmentById(appointment.getId());
            if (managedAppointment == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Appointment not found."));
                return;
            }
            managedAppointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentService.updateAppointment(managedAppointment);
            appointments = appointmentService.getAllActiveAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment marked as completed"));
            filterAppointments();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadCancelledAppointments() {
        if (!authBean.hasPermission("APPOINTMENT_VIEW_DELETED") && doctorAuthBean.getLoggedInDoctor() == null) {
            showAccessDeniedMessage();
            cancelledAppointments = new ArrayList<>();
            return;
        }
        if (doctorAuthBean.getLoggedInDoctor() != null) {
            // A doctor is logged in, so fetch only their cancelled appointments.
            Long doctorId = doctorAuthBean.getLoggedInDoctor().getId();
            cancelledAppointments = appointmentService.getDeletedAppointmentsByDoctor(doctorId);
        }
        cancelledAppointments = appointmentService.getDeletedAppointments();
    }

    public void rescheduleAppointment(Appointment appointment){
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            selectedAppointment = appointment;
            selectedAppointment.setStatus(AppointmentStatus.SCHEDULED);
            appointmentService.updateAppointment(selectedAppointment);
            appointments = appointmentService.getAllActiveAppointments();
            cancelledAppointments = appointmentService.getDeletedAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment rescheduled successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void editAppointment(Appointment appointment) {
        if (!authBean.hasPermission("APPOINTMENT_EDIT") && doctorAuthBean.getLoggedInDoctor() == null) {
            showAccessDeniedMessage();
            return;
        }
        // Step 1: Fetch a fresh, complete appointment object from the database ONCE.
        // This ensures we have the most up-to-date data to edit.
        Appointment managedAppointment = appointmentService.getAppointmentById(appointment.getId());
        if (managedAppointment == null) {
            // Handle the case where the appointment might have been deleted by another user.
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Not Found", "The selected appointment could not be found. It may have been deleted."));
            return;
        }
        // Step 2: Set ALL the bean properties that the dialog is bound to.
        this.selectedAppointment = managedAppointment;

        // --- THIS IS THE FIX ---
        // You must also set the objects for the p:autoComplete components.
        this.selectedPatient = managedAppointment.getPatient();
        this.selectedDoctor = managedAppointment.getDoctor();
        this.newAppointment = false;
        // That's it! All unnecessary ID fields and redundant database calls are removed.
    }

    public boolean isNewAppointment() {
        return newAppointment;
    }

    public void restoreAppointment(Long id) {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            appointmentService.restoreAppointment(id);
            appointments = appointmentService.getAllActiveAppointments();
            cancelledAppointments = appointmentService.getDeletedAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment restored successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public AppointmentStatus[] getStatuses() {
        return AppointmentStatus.values();
    }

    // Getters and Setters
    public List<Appointment> getAppointments() {return appointments;}
    public List<Appointment> getSelectedAppointments() { return selectedAppointments; }
    public void setSelectedAppointments(List<Appointment> selectedAppointments) { this.selectedAppointments = selectedAppointments; }
    public Appointment getSelectedAppointment() { return selectedAppointment; }
    public void setSelectedAppointment(Appointment selectedAppointment) { this.selectedAppointment = selectedAppointment; }
    public List<Patient> getPatients() {return patients;}
    public List<Doctor> getDoctors() {return doctors;}
    public List<Appointment> getCancelledAppointments() {return cancelledAppointments;}
    public Long getSelectedPatientId() { return selectedPatientId; }
    public void setSelectedPatientId(Long selectedPatientId) { this.selectedPatientId = selectedPatientId; }
    public Long getSelectedDoctorId() { return selectedDoctorId; }
    public void setSelectedDoctorId(Long selectedDoctorId) { this.selectedDoctorId = selectedDoctorId; }
    public String getSelectedPatientName() {
        return selectedPatientName;
    }
    public void setSelectedPatientName(String selectedPatientName) {
        this.selectedPatientName = selectedPatientName;
    }
    public String getSelectedDoctorName() {
        return selectedDoctorName;
    }
    public void setSelectedDoctorName(String selectedDoctorName) {
        this.selectedDoctorName = selectedDoctorName;
    }
    public Patient getSelectedPatient() { return selectedPatient; }
    public void setSelectedPatient(Patient selectedPatient) { this.selectedPatient = selectedPatient; }
    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public void setSelectedDoctor(Doctor selectedDoctor) { this.selectedDoctor = selectedDoctor; }
    public Long getAppointmentToDeleteId() { return appointmentToDeleteId; }
    public void setAppointmentToDeleteId(Long appointmentToDeleteId) { this.appointmentToDeleteId = appointmentToDeleteId; }

    public List<Patient> completePatient(String query) {
        if (query == null || query.isEmpty()) return patients;
        String lower = query.toLowerCase();
        return patients.stream()
            .filter(p -> (p.getFirstName() + " " + p.getLastName()).toLowerCase().contains(lower))
            .toList();
    }
    public List<Doctor> completeDoctor(String query) {
        if (query == null || query.isEmpty()) return doctors;
        String lower = query.toLowerCase();
        return doctors.stream()
            .filter(d -> (d.getFirstName() + " " + d.getLastName()).toLowerCase().contains(lower))
            .toList();
    }

    public void hardDeleteAppointment(Long id) {
        if (!isAdmin()) {
            showAccessDeniedMessage();
            return;
        }
        try {
            appointmentService.deleteAppointment(id); // Use hard delete method
            cancelledAppointments = appointmentService.getDeletedAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Appointment permanently deleted"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // --- NEW: Action method to apply filters ---
    public void filterAppointments() {
        System.out.println("Filtering appointments...");
        // Extract IDs from the selected filter objects
        Long patientId = (filterPatient != null) ? filterPatient.getId() : null;
        Long doctorId = (filterDoctor != null) ? filterDoctor.getId() : null;

        // Call the new service method with the filter values
        this.appointments = appointmentService.getFilteredAppointments(patientId, doctorId, filterStartDate, filterEndDate, filterStatus);
    }

    // --- NEW: Action method to clear filters ---
    public void clearFilters() {
        System.out.println("Clearing appointment filters.");
        this.filterPatient = null;
        this.filterDoctor = null;
        this.filterStartDate = null;
        this.filterEndDate = null;
        this.filterStatus = null;
        // Reload the list with no filters applied
        // --- THIS IS THE FIX ---
        // If a doctor is logged in, reset the filter TO that doctor.
        // Otherwise, for staff, clear the doctor filter completely.
        if (doctorAuthBean != null && doctorAuthBean.getLoggedInDoctor() != null) {
            this.filterDoctor = doctorAuthBean.getLoggedInDoctor();
        } else {
            this.filterDoctor = null;
        }
        filterAppointments();
    }

    // --- NEW: Getter for the status enum values for the dropdown ---
    //    an array ([]) containing AppointmentStatus objects.
    public AppointmentStatus[] getAppointmentStatuses() {
        return AppointmentStatus.values();
    }

    // ... all your other existing methods (initNewAppointment, saveAppointment, etc.) ...

    // --- NEW: Getters and Setters for the filter properties ---
    public Patient getFilterPatient() { return filterPatient; }
    public void setFilterPatient(Patient filterPatient) { this.filterPatient = filterPatient; }
    public Doctor getFilterDoctor() { return filterDoctor; }
    public void setFilterDoctor(Doctor filterDoctor) { this.filterDoctor = filterDoctor; }
    public LocalDateTime getFilterStartDate() { return filterStartDate; }
    public void setFilterStartDate(LocalDateTime filterStartDate) { this.filterStartDate = filterStartDate; }
    public LocalDateTime getFilterEndDate() { return filterEndDate; }
    public void setFilterEndDate(LocalDateTime filterEndDate) { this.filterEndDate = filterEndDate; }
    public String getFilterStatus() { return filterStatus; }
    public void setFilterStatus(String filterStatus) { this.filterStatus = filterStatus; }

}

