package com.pahappa.models.analytics;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "analysis_dimensions")
public class AnalysisDimension implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_point_id", nullable = false)
    private DataPoint dataPoint;

    @Column(name = "frequency_minutes", nullable = false)
    private int frequency; // Frequency of computation in minutes

    public AnalysisDimension() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DataPoint getDataPoint() { return dataPoint; }
    public void setDataPoint(DataPoint dataPoint) { this.dataPoint = dataPoint; }
    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisDimension that = (AnalysisDimension) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}