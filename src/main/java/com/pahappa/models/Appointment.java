package com.pahappa.models;
import com.pahappa.constants.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    /**
     * @EnumType.STRING: What it does: It stores the enum's name as plain text in the database.
     * <p>
     * •SCHEDULED is saved as the string "SCHEDULED".<p>
     * •COMPLETED is saved as the string "COMPLETED".<p>
     * •CANCELLED is saved as the string "CANCELLED".<p>
     * <p>
     * This is incredibly robust and readable.
     * You can reorder the enum constants all you want, and it won't affect the existing data.
     * You can also look directly at the database table and immediately understand what each status means
     * without having to cross-reference your Java code.
     * <p>
     * @.ORDINAL Unlike using .ORDINAL (the default) which stores position instead
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;


    @Column(name = "reason_for_visit")
    private String reasonForVisit;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // Constructors
    public Appointment() {}

    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentTime, AppointmentStatus status, Boolean isDeleted
    ) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.isDeleted = isDeleted != null ? isDeleted : false; // Default to false if null

    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isValidAppointmentTime() {
        return appointmentTime != null && !appointmentTime.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patient=" + patient.getFirstName() + " " + patient.getLastName() +
                ", doctor=" + doctor.getFirstName() + " " + doctor.getLastName() +
                ", appointmentTime=" + appointmentTime +
                ", status='" + status + '\'' +
                '}';
    }
}