package com.pahappa.models.analytics;

import com.pahappa.models.Staff;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dashboard_layouts")
public class DashboardLayout implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "My Custom Dashboard"

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Staff owner; // The user who created this layout

    @Column(name = "is_default")
    private boolean isDefault;

    @OneToMany(mappedBy = "layout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("position ASC")
    private List<DashboardWidgetEntity> widgets = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardLayout that = (DashboardLayout) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    // Getters, Setters, Constructors
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Staff getOwner() { return owner; }
    public void setOwner(Staff owner) { this.owner = owner; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public List<DashboardWidgetEntity> getWidgets() { return widgets; }
    public void setWidgets(List<DashboardWidgetEntity> widgets) { this.widgets = widgets; }
}
