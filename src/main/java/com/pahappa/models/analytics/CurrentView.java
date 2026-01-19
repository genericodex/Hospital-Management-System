package com.pahappa.models.analytics;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_views")
public class CurrentView implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OneToOne ensures only one current record per Analysis Dimension
    @OneToOne
    @JoinColumn(name = "analysis_dimension_id", unique = true, nullable = false)
    private AnalysisDimension analysisDimension;

    @Column(name = "result_value")
    private Double result;

    @Column(name = "computation_date", nullable = false)
    private LocalDateTime computationDate;

    @Column(nullable = false)
    private String status; // Active, Not active

    public CurrentView() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AnalysisDimension getAnalysisDimension() { return analysisDimension; }
    public void setAnalysisDimension(AnalysisDimension analysisDimension) { this.analysisDimension = analysisDimension; }
    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }
    public LocalDateTime getComputationDate() { return computationDate; }
    public void setComputationDate(LocalDateTime computationDate) { this.computationDate = computationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}