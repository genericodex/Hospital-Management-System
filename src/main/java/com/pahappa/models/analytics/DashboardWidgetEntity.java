package com.pahappa.models.analytics;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dashboard_widgets")
public class DashboardWidgetEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "layout_id")
    private DashboardLayout layout;

    @Column(nullable = false)
    private String title; // e.g., "Revenue by Month"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WidgetType type; // CARD, CHART, TABLE, CALENDAR

    @Enumerated(EnumType.STRING)
    private ChartType chartType; // BAR, LINE, PIE, DOUGHNUT (Only if type == CHART)

    // --- Data Source Configuration ---
    @Column(name = "data_source_table")
    private String dataSourceTable; // e.g., "Appointment", "Billing"

    @Column(name = "x_axis_field")
    private String xAxisField; // Field for grouping/labels (e.g., "appointmentTime", "status")

    @Column(name = "y_axis_field")
    private String yAxisField; // Field for value (e.g., "amount", "id")

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregation_type")
    private AggregationType aggregation; // COUNT, SUM, AVG

    @Column(name = "filter_clause")
    private String filterClause; // Optional HQL filter (e.g., "status = 'PAID'")

    @Column(name = "table_columns", length = 1000)
    private String tableColumns;

    @Column(name = "grid_position")
    private int position; // To order them on UI

    // Enums
    public enum WidgetType { CARD, CHART, TABLE, CALENDAR }
    public enum ChartType { BAR, LINE, PIE, DOUGHNUT, AREA }
    public enum AggregationType { COUNT, SUM, AVG, MIN, MAX }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DashboardLayout getLayout() { return layout; }
    public void setLayout(DashboardLayout layout) { this.layout = layout; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public WidgetType getType() { return type; }
    public void setType(WidgetType type) { this.type = type; }
    public ChartType getChartType() { return chartType; }
    public void setChartType(ChartType chartType) { this.chartType = chartType; }
    public String getDataSourceTable() { return dataSourceTable; }
    public void setDataSourceTable(String dataSourceTable) { this.dataSourceTable = dataSourceTable; }
    public String getXAxisField() { return xAxisField; }
    public void setXAxisField(String xAxisField) { this.xAxisField = xAxisField; }
    public String getYAxisField() { return yAxisField; }
    public void setYAxisField(String yAxisField) { this.yAxisField = yAxisField; }
    public AggregationType getAggregation() { return aggregation; }
    public void setAggregation(AggregationType aggregation) { this.aggregation = aggregation; }
    public String getFilterClause() { return filterClause; }
    public void setFilterClause(String filterClause) { this.filterClause = filterClause; }
    public String getTableColumns() { return tableColumns; }
    public void setTableColumns(String tableColumns) { this.tableColumns = tableColumns; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
