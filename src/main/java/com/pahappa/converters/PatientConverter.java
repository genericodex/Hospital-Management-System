package com.pahappa.converters;

import com.pahappa.models.Patient;
import com.pahappa.services.HospitalService;
import com.pahappa.services.patient.impl.PatientServiceImpl;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "patientConverter", managed = true)
public class PatientConverter implements Converter<Patient> {
    @Inject
    private PatientServiceImpl hospitalService;

    @Override
    public Patient getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.valueOf(value);
            return hospitalService.getPatientById(id);
        } catch (Exception e) {
            throw new jakarta.faces.convert.ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Could not convert patient: " + e.getMessage()));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Patient value) {
        if (value == null) return "";
        return String.valueOf(value.getId());
    }
}

