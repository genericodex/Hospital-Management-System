package com.pahappa.converters;

import com.pahappa.models.Patient;
import com.pahappa.services.HospitalService;
<<<<<<< Updated upstream
=======
import com.pahappa.services.PatientServiceImpl;
>>>>>>> Stashed changes
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "patientConverter", managed = true)
public class PatientConverter implements Converter<Patient> {
    @Inject
<<<<<<< Updated upstream
    private HospitalService hospitalService;
=======
    private PatientServiceImpl hospitalService;
>>>>>>> Stashed changes

    @Override
    public Patient getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.valueOf(value);
            return hospitalService.getPatientById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Patient value) {
        if (value == null) return "";
        return String.valueOf(value.getId());
    }
}

