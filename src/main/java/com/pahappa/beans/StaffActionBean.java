package com.pahappa.beans;

import com.pahappa.models.StaffAction;
import com.pahappa.services.StaffActionService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class StaffActionBean implements Serializable {
    private List<StaffAction> actions;
    private final StaffActionService staffActionService = new StaffActionService();

    @PostConstruct
    public void init() {
        actions = staffActionService.getAllActions();
    }

    public List<StaffAction> getActions() {
        return actions;
    }

    public void refresh() {
        actions = staffActionService.getAllActions();
    }
}

