package com.pahappa.services.patient;

import com.pahappa.models.Patient;
import com.pahappa.models.Staff;

import java.util.Date;
import java.util.List;

/**
 * @public: This is an access modifier. 'public' means that any other class
 * in your entire project is allowed to see and use this interface.
 * <p>
 * @interface: This is the most important keyword here. It declares that this
 * is not a class that contains real code, but a "contract" or a "blueprint".
 * It defines a set of rules and promises about WHAT can be done, not showing the implementation of the service
 * <p>
 * @PatientService: This is the name of our contract. The name clearly
 * states its purpose: to define all the business operations related to Patients, promoting abstraction
 */
public interface PatientService {
    /**
     * @'Patient' -> This method promises to return a complete Patient object after it's done.
     * <p>
     * @'createPatient' -> A clear, verb-first name describing the action.
     * Parameters: '(String firstName, ... Staff staff)' -> This is the list of ingredients or
     * information the method NEEDS to do its job. To create a patient, you must provide all these details.
     */
    Patient createPatient(String firstName, String lastName, Date dob, String contact, String address, String email, Boolean isDeleted, String medicalHistory, Staff staff);
    Patient getPatientById(Long id);

    /**
     * @'List' is an ordered collection of items (like a grocery list).
     * <p>
     * @<...> specifies that this list is TYPE-SAFE. It can ONLY hold e.g., in here, Patient objects.
     */
    List<Patient> getAllPatients();
    void updatePatient(Patient patient, Staff staff);
    void deletePatient(Long id, Staff staff);
    void softDeletePatient(Long id, Staff staff);
    void restorePatient(Long id, Staff staff);
    List<Patient> getAllActivePatient();
    List<Patient> getDeletedPatient();
    long countActivePatients();
}
