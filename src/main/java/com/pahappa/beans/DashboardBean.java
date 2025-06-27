package com.pahappa.beans;

import com.pahappa.models.Appointment;
import com.pahappa.services.HospitalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private HospitalService hospitalService;

    private long patientCount;
    private long doctorCount;
    private long appointmentCount;
    private long staffCount;
    private List<Appointment> recentAppointments;

    @PostConstruct
    public void init() {
        patientCount = hospitalService.getAllActivePatient().size();
        doctorCount = hospitalService.getAllActiveDoctor().size();
        appointmentCount = hospitalService.getAllActiveAppointments().size();
        staffCount = hospitalService.getAllActiveStaff().size();

        recentAppointments = hospitalService.getAllActiveAppointments().stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    // Getters only (no setters needed)
    public long getPatientCount() { return patientCount; }
    public long getDoctorCount() { return doctorCount; }
    public long getAppointmentCount() { return appointmentCount; }
    public long getStaffCount() { return staffCount; }
    public List<Appointment> getRecentAppointments() { return recentAppointments; }
}