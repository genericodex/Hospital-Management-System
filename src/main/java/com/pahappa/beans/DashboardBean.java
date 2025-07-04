package com.pahappa.beans;

import com.pahappa.models.Appointment;
import com.pahappa.services.PatientServiceImpl;
import com.pahappa.services.DoctorServiceImpl;
import com.pahappa.services.AppointmentServiceImpl;
import com.pahappa.services.StaffServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private PatientServiceImpl patientService;
    @Inject
    private DoctorServiceImpl doctorService;
    @Inject
    private AppointmentServiceImpl appointmentService;
    @Inject
    private StaffServiceImpl staffService;

    private long patientCount;
    private long doctorCount;
    private long appointmentCount;
    private long staffCount;
    private List<Appointment> recentAppointments;
    private int page = 0;
    private int totalPages = 1;
    private static final int PAGE_SIZE = 5;

    @PostConstruct
    public void init() {
        patientCount = patientService.getAllActivePatient().size();
        doctorCount = doctorService.getAllActiveDoctors().size();
        appointmentCount = appointmentService.getAllActiveAppointments().size();
        staffCount = staffService.getAllActiveStaff().size();

        // Sort by earliest first (ascending)
        List<Appointment> allAppointments = appointmentService.getAllActiveAppointments().stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .collect(Collectors.toList());
        int total = allAppointments.size();
        totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        recentAppointments = allAppointments;
    }

    public List<Appointment> getRecentAppointments() {
        int fromIndex = page * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, recentAppointments.size());
        if (fromIndex > toIndex) {
            return List.of();
        }
        return recentAppointments.subList(fromIndex, toIndex);
    }

    // Getters only (no setters needed)
    public long getPatientCount() { return patientCount; }
    public long getDoctorCount() { return doctorCount; }
    public long getAppointmentCount() { return appointmentCount; }
    public long getStaffCount() { return staffCount; }
    public int getPage() {
        return page;
    }
    public int getTotalPages() {
        return totalPages == 0 ? 1 : totalPages;
    }
    public void nextPage() {
        if (page + 1 < getTotalPages()) {
            page++;
        }
    }
    public void prevPage() {
        if (page > 0) {
            page--;
        }
    }
    public void setPage(int page) {
        this.page = page;
    }
}