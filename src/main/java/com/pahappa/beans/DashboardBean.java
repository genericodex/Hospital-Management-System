package com.pahappa.beans;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.constants.BillingStatus;
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
import java.util.*;
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

            // --- NEW: Logic for multi-line Revenue Chart (Paid vs. Pending) ---
            LocalDate revenueEndDate = LocalDate.now();
            LocalDate revenueStartDate = revenueEndDate.minusDays(29);
            List<Object[]> dailyRevenueByStatus = billingService.getDailyRevenueByStatus(revenueStartDate, revenueEndDate);

            // Process the raw data into a structured map for easy JSON conversion
            Map<LocalDate, Map<BillingStatus, Double>> processedData = new TreeMap<>();
            revenueStartDate.datesUntil(revenueEndDate.plusDays(1)).forEach(date -> {
                Map<BillingStatus, Double> statusMap = new EnumMap<>(BillingStatus.class);
                statusMap.put(BillingStatus.PAID, 0.0);
                statusMap.put(BillingStatus.PENDING, 0.0);
                processedData.put(date, statusMap);
            });

            for (Object[] row : dailyRevenueByStatus) {
                // The DAO returns java.sql.Date, which needs conversion to LocalDate
                LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                BillingStatus status = (BillingStatus) row[1];
                Double total = (Double) row[2];
                if (processedData.containsKey(date)) {
                    processedData.get(date).put(status, total == null ? 0.0 : total);
                }
            }

            // Build the separate lists for the final JSON object
            List<String> labels = new ArrayList<>();
            List<Double> paidData = new ArrayList<>();
            List<Double> pendingData = new ArrayList<>();

            processedData.forEach((date, statusMap) -> {
                labels.add(date.format(DateTimeFormatter.ofPattern("MMM dd")));
                paidData.add(statusMap.get(BillingStatus.PAID));
                pendingData.add(statusMap.get(BillingStatus.PENDING));
            });

            // Manually construct the final JSON object to be sent to the frontend
            String labelsJson = labels.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
            String paidDataJson = paidData.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
            String pendingDataJson = pendingData.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
            String revenueChartJson = String.format("{\"labels\": %s, \"paidData\": %s, \"pendingData\": %s}", labelsJson, paidDataJson, pendingDataJson);

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

            // --- NEW: Data for Billing Status Doughnut Chart ---
            List<Object[]> billingStatusData = billingService.getBillingStatusTotals();
            String billingStatusJson = billingStatusData.stream()
                    .map(row -> {
                        BillingStatus status = (BillingStatus) row[0];
                        Double total = (Double) row[1];
                        return String.format("{\"status\": \"%s\", \"total\": %.2f}", status.name(), (total != null ? total : 0.0));
                    })
                    .collect(Collectors.joining(", ", "[", "]"));

            // --- NEW: Data for Doctor Specialization Doughnut Chart ---
            List<Object[]> specializationData = doctorService.getSpecializationCounts();
            String specializationJson = specializationData.stream()
                    .map(row -> {
                        String specialization = (String) row[0];
                        Long count = (Long) row[1];
                        String escapedSpec = (specialization != null) ? specialization.replace("\"", "\\\"") : "Unassigned";
                        return String.format("{\"specialization\": \"%s\", \"count\": %d}", escapedSpec, count);
                    })
                    .collect(Collectors.joining(", ", "[", "]"));



            // UPDATE: Execute a JavaScript function passing ALL THREE JSON strings
            PrimeFaces.current().executeScript("initStaffDashboardCharts(" +
                    barChartJson + ", " +
                    doughnutChartJson + ", " +
                    revenueChartJson + ", " +
                    doctorWorkloadJson + ", " +
                    billingStatusJson + ", " +
                    specializationJson + ");");
            System.out.println("Successfully sent all chart data to the browser.");

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to load and send chart data."+ e);
        }
    }
}