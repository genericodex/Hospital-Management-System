package com.pahappa.models;
import com.pahappa.constants.AppointmentStatus;
import jakarta.persistence.*;
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
    private Date appointmentTime;

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

    public Appointment(Patient patient, Doctor doctor, Date appointmentTime, AppointmentStatus status, Boolean isDeleted
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

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
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

<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
    public boolean isValidAppointmentTime() {
        return appointmentTime != null && !appointmentTime.before(new java.util.Date());
    }

>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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