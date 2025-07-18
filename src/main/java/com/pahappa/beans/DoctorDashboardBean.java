package com.pahappa.beans;

import com.pahappa.constants.AppointmentStatus;
import com.pahappa.dao.AppointmentDao;
import com.pahappa.models.Appointment;
import com.pahappa.models.Patient;
import com.pahappa.services.appointment.AppointmentService;
import com.pahappa.services.patient.PatientService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.PrimeFaces; // Import PrimeFaces

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class DoctorDashboardBean implements Serializable {

    @Inject
    private DoctorAuthBean authBean;
    @Inject
    private transient PatientService patientService;
    @Inject
    private AppointmentService appointmentService;
    // Following the project's current pattern of manual DAO instantiation.
    @Inject
    private transient AppointmentDao appointmentDao;
    private LocalDate initialDate;
    private ScheduleModel scheduleModel;
    private Appointment selectedAppointment;
    private Patient selectedPatientForDetails;
    private List<Appointment> upcomingAppointments;

    @PostConstruct
    public void init() {
        appointmentDao = new AppointmentDao();
        scheduleModel = new DefaultScheduleModel();
        // Ensure the logged-in user is not null before proceeding
        if (authBean.getLoggedInDoctor() != null) {
            Long doctorId = authBean.getLoggedInDoctor().getId();
            loadInitialData(doctorId);
            this.upcomingAppointments = appointmentService.getUpcomingAppointmentsForDoctor(doctorId);        }
        else {
            // If not logged in for any reason, default calendar to today to prevent errors
            this.initialDate = LocalDate.now();
            this.upcomingAppointments = Collections.emptyList();
        }
    }

    private void loadInitialData(Long doctorId) {
        // Load data for both the schedule and the agenda list
        List<Appointment> allAppointments = appointmentService.getAppointmentsByDoctor(doctorId);
        this.upcomingAppointments = appointmentService.getUpcomingAppointmentsForDoctor(doctorId);
        createSchedule(allAppointments);
    }

    public void loadChartData() {
        if (authBean != null && authBean.getLoggedInDoctor() != null) {
            Long doctorId = authBean.getLoggedInDoctor().getId();
            Map<String, Long> statusCounts = appointmentDao.getAppointmentStatusCountsByDoctor(doctorId);
            // Convert the Java Map to a JSON string.
            String pieChartJson = statusCounts.entrySet().stream()
                    .map(entry -> String.format("{\"status\": \"%s\", \"count\": %d}", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(", ", "[", "]"));


            // --- NEW: Data for Weekly Activity Bar Chart ---
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);
            List<Object[]> dailyDataByStatus = appointmentService.getDailyAppointmentCountsByStatusForDoctor(doctorId, startDate, endDate);

            // Process the raw data into a structured map for easy JSON conversion
            Map<LocalDate, Map<AppointmentStatus, Long>> processedWeeklyData = new TreeMap<>();
            startDate.datesUntil(endDate.plusDays(1)).forEach(date -> {
                Map<AppointmentStatus, Long> statusMap = new EnumMap<>(AppointmentStatus.class);
                statusMap.put(AppointmentStatus.SCHEDULED, 0L);
                statusMap.put(AppointmentStatus.COMPLETED, 0L);
                statusMap.put(AppointmentStatus.CANCELLED, 0L);
                processedWeeklyData.put(date, statusMap);
            });

            for (Object[] row : dailyDataByStatus) {
                LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                AppointmentStatus status = (AppointmentStatus) row[1];
                Long count = (Long) row[2];
                if (processedWeeklyData.containsKey(date)) {
                    processedWeeklyData.get(date).put(status, count);
                }
            }

            String barChartJson = buildDoctorStackedBarChartJson(processedWeeklyData);

            // Send both JSON strings to the frontend
            PrimeFaces.current().executeScript("initDoctorDashboardCharts(" + pieChartJson + ", " + barChartJson + ");");
        }
    }

    /**
     * A private helper method to build the JSON for the doctor's stacked bar chart.
     * This keeps the main loadChartData() method cleaner and more organized.
     */
    private String buildDoctorStackedBarChartJson(Map<LocalDate, Map<AppointmentStatus, Long>> processedData) {
        List<String> labels = new ArrayList<>();
        List<Long> scheduledData = new ArrayList<>();
        List<Long> completedData = new ArrayList<>();
        List<Long> cancelledData = new ArrayList<>();

        processedData.forEach((date, statusMap) -> {
            labels.add(date.format(DateTimeFormatter.ofPattern("EEE"))); // e.g., "Mon"
            scheduledData.add(statusMap.getOrDefault(AppointmentStatus.SCHEDULED, 0L));
            completedData.add(statusMap.getOrDefault(AppointmentStatus.COMPLETED, 0L));
            cancelledData.add(statusMap.getOrDefault(AppointmentStatus.CANCELLED, 0L));
        });

        String labelsJson = labels.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
        String scheduledJson = scheduledData.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
        String completedJson = completedData.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
        String cancelledJson = cancelledData.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));

        return String.format("{\"labels\": %s, \"scheduled\": %s, \"completed\": %s, \"cancelled\": %s}",
                labelsJson, scheduledJson, completedJson, cancelledJson);
    }

    private void createSchedule(List<Appointment> appointments) {
        scheduleModel.clear();
        for (Appointment appt : appointments) {
            // FEATURE: Add richer styling to the calendar event
            DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
                    .title(" Patient: " + appt.getPatient().getFirstName())
                    .startDate(appt.getAppointmentTime())
                    .endDate(appt.getAppointmentTime().plusHours(1))
                    .styleClass(getEventStyleClass(appt.getStatus().name()))
                    .backgroundColor(getEventBackgroundColor(appt.getStatus().name()))
                    .borderColor(getEventBorderColor(appt.getStatus().name()))
                    .textColor("#ffffff") // White text provides good contrast
                    .editable(false)
                    .data(appt.getId())
                    .build();
            scheduleModel.addEvent(event);
        }
    }

    /**
     * This method is called by the p:ajax tag when a calendar event is clicked.
     */
    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        ScheduleEvent<?> event = selectEvent.getObject();
        if (event != null && event.getData() != null) {
            Long appointmentId = (Long) event.getData();
            this.selectedAppointment = appointmentService.findById(appointmentId);
        }
    }

    // --- NEW: Helper method to return a CSS class name ---
    private String getEventStyleClass(String status) {
        switch (status) {
            case "SCHEDULED": return "status-scheduled";
            case "COMPLETED": return "status-completed";
            case "CANCELLED": return "status-cancelled";
            default: return "status-default";
        }
    }

    public void updateSelectedAppointment() {
        if (selectedAppointment != null) {
            try {
                appointmentService.updateCalendarAppointment(selectedAppointment);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Appointment updated."));

                // Refresh all data on the dashboard to reflect the change
                loadInitialData(authBean.getLoggedInDoctor().getId());
                PrimeFaces.current().executeScript("PF('appointmentDialogWidget').hide();");
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update appointment."));
            }
        }
    }

    // NEW: Methods for richer event colors
    private String getEventBackgroundColor(String status) {
        switch (status) {
            case "SCHEDULED": return "#cdcdcd"; // Tailwind Blue 600
            case "COMPLETED": return "#16a34a"; // Tailwind Green 600
            case "CANCELLED": return "#dc2626"; // Tailwind Red 600
            default: return "#6b7280";      // Tailwind Gray 500
        }
    }

    private String getEventBorderColor(String status) {
        // A slightly darker shade for the border provides a nice depth effect
        switch (status) {
            case "SCHEDULED": return "#70e6e6"; // Tailwind Blue 700
            case "COMPLETED": return "#53ff9b"; // Tailwind Green 700
            case "CANCELLED": return "#b91c1c"; // Tailwind Red 700
            default: return "#4b5563";      // Tailwind Gray 600
        }
    }

    /**
     * NEW: This method is called when the "View Record" button in the agenda is clicked.
     * It fetches a fresh Patient object to show in the dialog.
     */
    public void selectPatientForDetails(Patient patient) {
        if (patient != null && patient.getId() != null) {
            // It's a good practice to re-fetch the entity to ensure all data is loaded
            // and to avoid lazy loading issues, especially if the Patient has more relationships.
            // NOTE: This assumes you have a findById method in your PatientService.
            this.selectedPatientForDetails = patientService.getPatientById(patient.getId());
        }
    }

    public List<Appointment> getUpcomingAppointments() {return upcomingAppointments;}
    public ScheduleModel getScheduleModel() {return scheduleModel;}
    public LocalDate getInitialDate() {return initialDate;}
    public Patient getSelectedPatientForDetails() { return selectedPatientForDetails; }
    public void setSelectedPatientForDetails(Patient selectedPatientForDetails) { this.selectedPatientForDetails = selectedPatientForDetails; }
    public Appointment getSelectedAppointment() {return selectedAppointment;}
    public void setSelectedAppointment(Appointment selectedAppointment) {this.selectedAppointment = selectedAppointment;}
}
