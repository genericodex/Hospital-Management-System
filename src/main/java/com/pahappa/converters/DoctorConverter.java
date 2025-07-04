package com.pahappa.converters;

import com.pahappa.models.Doctor;
<<<<<<< Updated upstream
=======
import com.pahappa.services.DoctorServiceImpl;
>>>>>>> Stashed changes
import com.pahappa.services.HospitalService;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "doctorConverter", managed = true)
public class DoctorConverter implements Converter<Doctor> {
    @Inject
<<<<<<< Updated upstream
    private HospitalService hospitalService;
=======
    private DoctorServiceImpl hospitalService;
>>>>>>> Stashed changes

    @Override
    public Doctor getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.valueOf(value);
            return hospitalService.getDoctorById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Doctor value) {
        if (value == null) return "";
        return String.valueOf(value.getId());
    }
}

