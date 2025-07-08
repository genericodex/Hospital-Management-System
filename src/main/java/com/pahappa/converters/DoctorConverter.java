package com.pahappa.converters;

import com.pahappa.models.Doctor;
import com.pahappa.services.doctor.impl.DoctorServiceImpl;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "doctorConverter", managed = true)
public class DoctorConverter implements Converter<Doctor> {
    @Inject
    private DoctorServiceImpl hospitalService;

    @Override
    public Doctor getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.valueOf(value);
            return hospitalService.getDoctorById(id);
        } catch (Exception e) {
            throw new jakarta.faces.convert.ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Could not convert doctor: " + e.getMessage()));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Doctor value) {
        if (value == null) return "";
        return String.valueOf(value.getId());
    }
}

