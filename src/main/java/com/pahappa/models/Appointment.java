package com.pahappa.models;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "appointment_time", nullable = false)
    private Date appointmentTime;

    @Column(nullable = false)
    private String status; // SCHEDULED, CANCELLED, COMPLETED

    @Column(name = "reason_for_visit")
    private String reasonForVisit;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public Appointment() {}

    public Appointment(Patient patient, Doctor doctor, Date appointmentTime, String status) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
        this.status = status;
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

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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