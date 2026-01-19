package com.pahappa.models.analytics;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "data_points")
public class DataPoint implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String query; // HQL or SQL query returning a single numerical value

    @Column(columnDefinition = "TEXT")
    private String params; // Placeholder for JSON or CSV parameters

    public DataPoint() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPoint dataPoint = (DataPoint) o;
        return Objects.equals(id, dataPoint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}