package com.pahappa.beans;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.models.Appointment;
import com.pahappa.models.Doctor;
import com.pahappa.models.Patient;
import com.pahappa.services.AppointmentServiceImpl;
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

@Named
@ViewScoped
public class AppointmentBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private AppointmentServiceImpl appointmentService;

    @Inject
    private DoctorAuthBean doctorAuthBean;

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

    @PostConstruct
    public void init() {
        // Defensive: ensure all injected beans are not null
        if (appointmentService == null) {
            appointments = new ArrayList<>();
            patients = new ArrayList<>();
            doctors = new ArrayList<>();
            selectedAppointment = new Appointment();
            return;
        }
        if (doctorAuthBean != null && doctorAuthBean.getLoggedInDoctor() != null) {
            try {
                appointments = appointmentService.getAppointmentsByDoctor(doctorAuthBean.getLoggedInDoctor().getId());
            } catch (Exception e) {
                appointments = new ArrayList<>();
            }
        } else {
            try {
                appointments = appointmentService.getAllActiveAppointments();
            } catch (Exception e) {
                appointments = new ArrayList<>();
            }
        }
        if (appointments == null) {
            appointments = new ArrayList<>();
        }
        try {
            patients = appointmentService.getAllPatients();
        } catch (Exception e) {
            patients = new ArrayList<>();
        }
        if (patients == null) {
            patients = new ArrayList<>();
        }
        try {
            doctors = appointmentService.getAllDoctors();
        } catch (Exception e) {
            doctors = new ArrayList<>();
        }
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
            // Backend validation: appointment cannot be in the past
            if (!selectedAppointment.isValidAppointmentTime()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Appointment date/time cannot be in the past."));
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
            Patient managedPatient = appointmentService.getPatientById(selectedPatientId);
            Doctor managedDoctor = appointmentService.getDoctorById(selectedDoctorId);
            if (selectedAppointment.getId() == null) {
                // Create new appointment
                appointmentService.createAppointment(
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
                Appointment managedAppointment = appointmentService.getAppointmentById(selectedAppointment.getId());
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
                appointmentService.updateAppointment(managedAppointment);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Appointment updated successfully"));
            }
            appointments = appointmentService.getAllActiveAppointments();
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
            appointmentService.cancelAppointment(appointment.getId());
            appointments = appointmentService.getAllActiveAppointments();
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
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void loadCancelledAppointments() {
        cancelledAppointments = appointmentService.getDeletedAppointments();
    }

    public void rescheduleAppointment(Appointment appointment) {
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
        this.selectedAppointment = appointmentService.getAppointmentById(appointment.getId());
        this.selectedPatientId = this.selectedAppointment.getPatient() != null ? this.selectedAppointment.getPatient().getId() : null;
        this.selectedDoctorId = this.selectedAppointment.getDoctor() != null ? this.selectedAppointment.getDoctor().getId() : null;
    }

    public void restoreAppointment(Long id) {
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
}
