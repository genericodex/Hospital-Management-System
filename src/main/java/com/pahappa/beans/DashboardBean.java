package com.pahappa.beans;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.models.Appointment;

import com.pahappa.services.billing.BillingService;
import com.pahappa.services.patient.PatientService;
import com.pahappa.services.doctor.DoctorService;
import com.pahappa.services.appointment.AppointmentService;
import com.pahappa.services.staff.StaffService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

   @Inject
   private transient PatientService patientService;
   @Inject
   private transient DoctorService doctorService;
   @Inject
   private transient AppointmentService appointmentService;
   @Inject
   private transient StaffService staffService;
   @Inject
   private transient BillingService billingService;


    private long patientCount;
    private long doctorCount;
    private long appointmentCount;
    private long staffCount;
    private double totalRevenue;
    // NEW: Property to control the number of recent appointments shown
    private int recentCount = 5; // Default to 5
    // NEW: Property to hold the appointment selected for the dialog
    private Appointment selectedAppointment;

    private List<Appointment> recentAppointments;

    @PostConstruct
    public void init() {
        try {
            this.patientCount = patientService.countActivePatients();
            this.doctorCount = doctorService.countActiveDoctors();
            this.staffCount = staffService.countActiveStaff();
            this.appointmentCount = appointmentService.countActiveAppointments();
            this.totalRevenue = billingService.getTotalRevenue();

            System.out.println("DEBUG: Patient Count: " + patientCount);
            System.out.println("DEBUG: Doctor Count: " + doctorCount);
            System.out.println("DEBUG: Appointment Count: " + appointmentCount);
            System.out.println("DEBUG: Staff Count: " + staffCount);

            refreshRecentAppointmentsList();
            System.out.println("DEBUG: Recent Appointments Size: " + recentAppointments.size());
        } catch (Exception e) {
            System.err.println("ERROR: Exception during DashboardBean initialization: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dashboard could not be loaded."));
            e.printStackTrace();
            // Initialize counts to 0 and lists to empty to prevent NullPointerExceptions
            patientCount = 0;
            doctorCount = 0;
            appointmentCount = 0;
            staffCount = 0;
            totalRevenue = 0.0;
            recentAppointments = Collections.emptyList();
        }
    }


    public void refreshRecentAppointmentsList() {
        try {
            this.recentAppointments = appointmentService.findRecentAppointments(recentCount);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Could not refresh appointments."));
            this.recentAppointments = Collections.emptyList();
        }
    }

    public List<Appointment> getRecentAppointments() {
        return recentAppointments;
    }

    // Getters only (no setters needed)
    public long getPatientCount() { return patientCount; }
    public long getDoctorCount() { return doctorCount; }
    public long getAppointmentCount() { return appointmentCount; }
    public long getStaffCount() { return staffCount; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getRecentCount() { return recentCount; }
    public void setRecentCount(int recentCount) { this.recentCount = recentCount; }
    public Appointment getSelectedAppointment() { return selectedAppointment; }
    public void setSelectedAppointment(Appointment selectedAppointment) { this.selectedAppointment = selectedAppointment; }

    public void onRowSelect(SelectEvent<Appointment> event) {
        this.selectedAppointment = event.getObject();
    }

    public void updateSelectedAppointment() {
        if (selectedAppointment != null) {
            try {
                // Assumes an 'update' method exists in your AppointmentService
                appointmentService.updateAppointment(selectedAppointment);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Appointment for " + selectedAppointment.getPatient().getFirstName() + " updated."));

                refreshRecentAppointmentsList(); // Refresh the list to show status changes
                PrimeFaces.current().executeScript("PF('appointmentDialogWidget').hide();");
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update appointment."));
            }
        }
    }
    /**
     * This method is called from the frontend by p:remoteCommand.
     * It fetches data, converts it to JSON, and sends it to a JavaScript function.
     */
    public void loadChartData() {
        System.out.println("Loading chart data for staff dashboard...");
        try {
            // Data for Bar Chart (Weekly Volume)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);
            Map<LocalDate, Long> dailyData = appointmentService.getDailyAppointmentCounts(startDate, endDate);

            String barChartJson = startDate.datesUntil(endDate.plusDays(1))
                    .map(date -> {
                        String formattedDate = date.format(DateTimeFormatter.ofPattern("EEE"));
                        long count = dailyData.getOrDefault(date, 0L);
                        return String.format("{\"date\": \"%s\", \"count\": %d}", formattedDate, count);
                    })
                    .collect(Collectors.joining(", ", "[", "]"));

            // Data for Doughnut Chart (Status Breakdown)
            Map<AppointmentStatus, Long> statusData = appointmentService.getGlobalAppointmentStatusCounts();
            String doughnutChartJson = statusData.entrySet().stream()
                    .map(entry -> {
                        String statusLabel = entry.getKey().getDisplayName();
                        return String.format("{\"status\": \"%s\", \"count\": %d}", statusLabel, entry.getValue());
                    })
                    .collect(Collectors.joining(", ", "[", "]"));

            // Data for Line Chart (Revenue)
            LocalDate revenueEndDate = LocalDate.now();
            LocalDate revenueStartDate = revenueEndDate.minusDays(29); // ~30 days
            Map<LocalDate, Double> dailyRevenueData = billingService.getDailyRevenue(revenueStartDate, revenueEndDate);
            String revenueChartJson = revenueStartDate.datesUntil(revenueEndDate.plusDays(1))
                    .map(date -> {
                        String formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd"));
                        double amount = dailyRevenueData.getOrDefault(date, 0.0);
                        return String.format("{\"date\": \"%s\", \"amount\": %.2f}", formattedDate, amount);
                    })
                    .collect(Collectors.joining(", ", "[", "]"));

            List<Object[]> doctorWorkloadData = appointmentService.getAppointmentCountsByDoctor();
            String doctorWorkloadJson = doctorWorkloadData.stream()
                    .map(row -> {
                        String doctorName = "Dr. " + row[0] + " " + row[1];
                        long count = (long) row[2];
                        // We escape the doctor's name to prevent issues with names like O'Malley
                        return String.format("{\"doctorName\": \"%s\", \"count\": %d}",
                                doctorName.replace("\"", "\\\""), count);
                    })
                    .collect(Collectors.joining(", ", "[", "]"));


            // UPDATE: Execute a JavaScript function passing ALL THREE JSON strings
            PrimeFaces.current().executeScript("initStaffDashboardCharts(" +
                    barChartJson + ", " +
                    doughnutChartJson + ", " +
                    revenueChartJson + ", " +
                    doctorWorkloadJson + ");");
            System.out.println("Successfully sent all chart data to the browser.");

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to load and send chart data."+ e);
        }
    }
}