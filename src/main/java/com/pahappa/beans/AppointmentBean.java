package com.pahappa.beans;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;
import com.pahappa.services.HospitalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AppointmentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private HospitalService hospitalService;

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

    @PostConstruct
    public void init() {
        appointments = hospitalService.getAllActiveAppointments();
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
        patients = hospitalService.getAllPatients();
        if (patients == null) {
            patients = new ArrayList<>();
        }
        doctors = hospitalService.getAllDoctors();
        if (doctors == null) {
            doctors = new ArrayList<>();
        }
        selectedAppointment = new Appointment();
    }

    public void initNewAppointment() {
        this.selectedAppointment = new Appointment();
        this.selectedPatientId = null;
        this.selectedDoctorId = null;
    }

    public void saveAppointment() {
        try {
            // Set IDs from selected objects
            if (selectedPatient != null) selectedPatientId = selectedPatient.getId();
            if (selectedDoctor != null) selectedDoctorId = selectedDoctor.getId();

            // Validation for required fields
            if (selectedAppointment.getAppointmentTime() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a date and time."));
                return;
            }
            if (selectedAppointment.getReasonForVisit() == null || selectedAppointment.getReasonForVisit().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please enter a reason for the visit."));
                return;
            }
            if (selectedPatientId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a patient."));
                return;
            }
            if (selectedDoctorId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a doctor."));
                return;
            }
            Patient managedPatient = hospitalService.getPatientById(selectedPatientId);
            Doctor managedDoctor = hospitalService.getDoctorById(selectedDoctorId);
            if (selectedAppointment.getId() == null) {
                // Create new appointment
                hospitalService.createAppointment(
                        managedPatient,
                        managedDoctor,
                        selectedAppointment.getAppointmentTime(),
                        selectedAppointment.getReasonForVisit(),
                        false
                );
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Appointment created successfully"));
            } else {
                // Update existing appointment
                Appointment managedAppointment = hospitalService.getAppointmentById(selectedAppointment.getId());
                if (managedAppointment == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Appointment not found for update."));
                    return;
                }
                managedAppointment.setAppointmentTime(selectedAppointment.getAppointmentTime());
                managedAppointment.setReasonForVisit(selectedAppointment.getReasonForVisit());
                managedAppointment.setPatient(managedPatient);
                managedAppointment.setDoctor(managedDoctor);
                managedAppointment.setStatus(selectedAppointment.getStatus());
                managedAppointment.setDeleted(selectedAppointment.isDeleted());
                hospitalService.updateAppointment(managedAppointment);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Appointment updated successfully"));
            }
            appointments = hospitalService.getAllActiveAppointments();
            // Reset selectedAppointment after save/update
            selectedAppointment = new Appointment();
            selectedPatientId = null;
            selectedDoctorId = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void cancelAppointment(Appointment appointment) {
        try {
            hospitalService.cancelAppointment(appointment.getId());
            appointments = hospitalService.getAllActiveAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment cancelled successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void cancelSelectedAppointments() {
        try {
            for (Appointment appointment : selectedAppointments) {
                hospitalService.cancelAppointment(appointment.getId());
            }
            appointments = hospitalService.getAllActiveAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(selectedAppointments.size() + " appointments cancelled successfully"));
            selectedAppointments = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void completeAppointment(Appointment appointment) {
        try {
            Appointment managedAppointment = hospitalService.getAppointmentById(appointment.getId());
            if (managedAppointment == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Appointment not found."));
                return;
            }
            managedAppointment.setStatus(AppointmentStatus.COMPLETED);
            hospitalService.updateAppointment(managedAppointment);
            appointments = hospitalService.getAllActiveAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment marked as completed"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadCancelledAppointments() {
        cancelledAppointments = hospitalService.getDeletedAppointments();
    }

    public void rescheduleAppointment(Appointment appointment) {
        try {
            selectedAppointment = appointment;
            selectedAppointment.setStatus(AppointmentStatus.SCHEDULED);
            hospitalService.updateAppointment(selectedAppointment);
            appointments = hospitalService.getAllActiveAppointments();
            cancelledAppointments = hospitalService.getDeletedAppointments();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Appointment rescheduled successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void editAppointment(Appointment appointment) {
        this.selectedAppointment = hospitalService.getAppointmentById(appointment.getId());
        this.selectedPatientId = this.selectedAppointment.getPatient() != null ? this.selectedAppointment.getPatient().getId() : null;
        this.selectedDoctorId = this.selectedAppointment.getDoctor() != null ? this.selectedAppointment.getDoctor().getId() : null;
    }

    public void restoreAppointment(Long id) {
        try {
            hospitalService.restoreAppointment(id);
            appointments = hospitalService.getAllActiveAppointments();
            cancelledAppointments = hospitalService.getDeletedAppointments();
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
    public List<Appointment> getAppointments() {
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
        return appointments;
    }
    public List<Appointment> getSelectedAppointments() { return selectedAppointments; }
    public void setSelectedAppointments(List<Appointment> selectedAppointments) { this.selectedAppointments = selectedAppointments; }
    public Appointment getSelectedAppointment() { return selectedAppointment; }
    public void setSelectedAppointment(Appointment selectedAppointment) { this.selectedAppointment = selectedAppointment; }
    public List<Patient> getPatients() { return patients; }
    public List<Doctor> getDoctors() { return doctors; }
    public List<Appointment> getCancelledAppointments() { return cancelledAppointments; }
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
}
