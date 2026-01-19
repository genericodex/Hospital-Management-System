package com.pahappa.beans;

import com.google.gson.Gson;
import com.pahappa.models.Staff;
import com.pahappa.models.analytics.DashboardLayout;
import com.pahappa.models.analytics.DashboardWidgetEntity;
import com.pahappa.services.dashboard.DynamicQueryService;
import com.pahappa.util.HibernateUtil;
import com.pahappa.util.SchemaMetadataProvider;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private DynamicQueryService queryService;
    @Inject
    private SchemaMetadataProvider schemaProvider;
    @Inject
    private AuthBean authBean;

    private DashboardLayout currentLayout;
    private List<DashboardLayout> availableLayouts;
    private Long currentLayoutId;

    private DashboardWidgetEntity newWidget;
    private List<String> availableTables;
    private List<String> availableFields;
    private boolean isEditMode = false;

    // Bind UI multi-select to this list, then convert to CSV string for entity
    private List<String> selectedTableColumns;

    private String newDashboardName;

    // --- PREVIEW FIELDS ---
    private String previewHql;
    private List<Map<String, Object>> previewDataRows;
    private List<String> previewDataColumns;

    @PostConstruct
    public void init() {
        loadUserDashboards();
        initNewWidget();
        availableTables = schemaProvider.getAvailableTables();
    }

    public void loadUserDashboards() {
        availableLayouts = new ArrayList<>();
        Staff currentUser = (authBean != null) ? authBean.getStaff() : null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DashboardLayout dl";
            if (currentUser != null) {
                hql += " WHERE dl.owner.id = :ownerId";
                availableLayouts = session.createQuery(hql, DashboardLayout.class)
                        .setParameter("ownerId", currentUser.getId())
                        .list();
            } else {
                availableLayouts = session.createQuery(hql, DashboardLayout.class).list();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (availableLayouts.isEmpty()) {
            createDefaultDashboard();
        } else {
            currentLayout = availableLayouts.stream()
                    .filter(DashboardLayout::isDefault)
                    .findFirst()
                    .orElse(availableLayouts.get(0));
            currentLayoutId = currentLayout.getId();
        }
    }

    private void createDefaultDashboard() {
        DashboardLayout defaultLayout = new DashboardLayout();
        defaultLayout.setName("Default Dashboard");
        defaultLayout.setDefault(true);
        if (authBean != null) defaultLayout.setOwner(authBean.getStaff());

        saveLayoutToDb(defaultLayout);
        availableLayouts.add(defaultLayout);
        currentLayout = defaultLayout;
        currentLayoutId = defaultLayout.getId();
    }

    public void createNewDashboard() {
        if (newDashboardName == null || newDashboardName.trim().isEmpty()) {
            addError("Dashboard name is required");
            return;
        }
        DashboardLayout layout = new DashboardLayout();
        layout.setName(newDashboardName);
        if (authBean != null) layout.setOwner(authBean.getStaff());

        saveLayoutToDb(layout);
        availableLayouts.add(layout);
        currentLayout = layout;
        currentLayoutId = layout.getId();
        newDashboardName = null;

        addInfo("Dashboard Created");
        PrimeFaces.current().executeScript("PF('newDashboardDialog').hide()");
        loadChartData();
    }

    private void saveLayoutToDb(DashboardLayout layout) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(layout);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            addError("Failed to save dashboard: " + e.getMessage());
        }
    }

    public void switchLayout() {
        if (currentLayoutId != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                currentLayout = session.get(DashboardLayout.class, currentLayoutId);
            }
            loadChartData();
        }
    }

    public void initNewWidget() {
        newWidget = new DashboardWidgetEntity();
        newWidget.setType(DashboardWidgetEntity.WidgetType.CHART);
        newWidget.setChartType(DashboardWidgetEntity.ChartType.BAR);
        isEditMode = false;
        availableFields = new ArrayList<>();
        selectedTableColumns = new ArrayList<>();
        clearPreview();
    }

    public void editWidget(DashboardWidgetEntity widget) {
        this.newWidget = widget;
        this.isEditMode = true;
        if (newWidget.getDataSourceTable() != null) {
            availableFields = schemaProvider.getFieldsForTable(newWidget.getDataSourceTable());
        }
        // Populate selected columns from CSV string
        if (widget.getTableColumns() != null && !widget.getTableColumns().isEmpty()) {
            selectedTableColumns = new ArrayList<>(Arrays.asList(widget.getTableColumns().split(",")));
        } else {
            selectedTableColumns = new ArrayList<>();
        }
        clearPreview();
    }

    private void clearPreview() {
        previewHql = null;
        previewDataRows = new ArrayList<>();
        previewDataColumns = new ArrayList<>();
    }

    public void deleteWidget(DashboardWidgetEntity widget) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. Load the parent Layout
            DashboardLayout layout = session.get(DashboardLayout.class, currentLayout.getId());

            // 2. Remove the widget from the parent's collection
            boolean removed = layout.getWidgets().removeIf(w -> w.getId().equals(widget.getId()));

            if (removed) {
                session.merge(layout);
            }

            tx.commit();

            // 3. Update in-memory list
            currentLayout.getWidgets().removeIf(w -> w.getId().equals(widget.getId()));
            loadChartData();
            addInfo("Widget Removed");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            addError("Error deleting widget: " + e.getMessage());
        }
    }

    public void onTableChange() {
        if (newWidget.getDataSourceTable() != null) {
            availableFields = schemaProvider.getFieldsForTable(newWidget.getDataSourceTable());
        }
    }

    // --- PREVIEW LOGIC START ---
    public void runWidgetPreview() {
        clearPreview();

        if (newWidget.getDataSourceTable() == null) {
            addError("Please select a Data Source table.");
            return;
        }

        // 1. Sync Columns for Table Type (needed for query generation)
        if (newWidget.getType() == DashboardWidgetEntity.WidgetType.TABLE) {
            if (selectedTableColumns != null && !selectedTableColumns.isEmpty()) {
                newWidget.setTableColumns(String.join(",", selectedTableColumns));
            } else {
                addError("Select at least one column for Table widget.");
                return;
            }
        }

        // 2. Generate Pseudo-HQL for display
        generatePreviewHql();

        // 3. Execute Data Preview
        try {
            Object result = queryService.executeWidgetQuery(newWidget);
            processPreviewData(result);
        } catch (Exception e) {
            clearPreview();
            addError("Query Execution Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePreviewHql() {
        StringBuilder sb = new StringBuilder();
        String table = newWidget.getDataSourceTable();
        String filter = newWidget.getFilterClause();

        switch (newWidget.getType()) {
            case CHART:
                sb.append("SELECT ").append(newWidget.getXAxisField()).append(", ")
                        .append(newWidget.getAggregation()).append("(").append(newWidget.getYAxisField()).append(") ")
                        .append("FROM ").append(table);
                if (filter != null && !filter.isEmpty()) sb.append(" WHERE ").append(filter);
                sb.append(" GROUP BY ").append(newWidget.getXAxisField());
                break;
            case CARD:
                sb.append("SELECT ").append(newWidget.getAggregation()).append("(").append(newWidget.getYAxisField()).append(") ")
                        .append("FROM ").append(table);
                if (filter != null && !filter.isEmpty()) sb.append(" WHERE ").append(filter);
                break;
            case TABLE:
                sb.append("SELECT ").append(newWidget.getTableColumns()).append(" FROM ").append(table);
                if (filter != null && !filter.isEmpty()) sb.append(" WHERE ").append(filter);
                break;
            case CALENDAR:
                sb.append("SELECT ").append(newWidget.getXAxisField()).append(", ").append(newWidget.getYAxisField())
                        .append(" FROM ").append(table);
                if (filter != null && !filter.isEmpty()) sb.append(" WHERE ").append(filter);
                break;
        }
        this.previewHql = sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void processPreviewData(Object result) {
        if (result == null) return;

        if (newWidget.getType() == DashboardWidgetEntity.WidgetType.CHART) {
            Map<?, ?> map = (Map<?, ?>) result;
            previewDataColumns = Arrays.asList("Label (" + newWidget.getXAxisField() + ")", "Value");
            int count = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (count++ >= 5) break; // Limit to 5 rows
                Map<String, Object> row = new HashMap<>();
                row.put(previewDataColumns.get(0), entry.getKey() != null ? entry.getKey().toString() : "NULL");
                row.put(previewDataColumns.get(1), entry.getValue());
                previewDataRows.add(row);
            }
        } else if (newWidget.getType() == DashboardWidgetEntity.WidgetType.CARD) {
            previewDataColumns = Collections.singletonList("Result");
            Map<String, Object> row = new HashMap<>();
            row.put("Result", formatNumber(result));
            previewDataRows.add(row);
        } else if (newWidget.getType() == DashboardWidgetEntity.WidgetType.TABLE) {
            Map<String, Object> tableData = (Map<String, Object>) result;
            List<String> headers = (List<String>) tableData.get("headers");
            List<List<Object>> rows = (List<List<Object>>) tableData.get("rows");

            if (headers != null) previewDataColumns = headers;
            if (rows != null) {
                int count = 0;
                for (List<Object> rowData : rows) {
                    if (count++ >= 5) break;
                    Map<String, Object> rowMap = new HashMap<>();
                    for (int i = 0; i < headers.size() && i < rowData.size(); i++) {
                        rowMap.put(headers.get(i), rowData.get(i));
                    }
                    previewDataRows.add(rowMap);
                }
            }
        } else if (newWidget.getType() == DashboardWidgetEntity.WidgetType.CALENDAR) {
            List<Map<String, String>> events = (List<Map<String, String>>) result;
            previewDataColumns = Arrays.asList("Date", "Title");
            int count = 0;
            for (Map<String, String> evt : events) {
                if (count++ >= 5) break;
                Map<String, Object> row = new HashMap<>();
                row.put("Date", evt.getOrDefault("start", ""));
                row.put("Title", evt.getOrDefault("title", ""));
                previewDataRows.add(row);
            }
        }
    }
    // --- PREVIEW LOGIC END ---

    public void saveWidget() {
        // Validation
        if (newWidget.getTitle() == null || newWidget.getTitle().trim().isEmpty()) {
            addError("Title is required");
            return;
        }
        if (newWidget.getDataSourceTable() == null) {
            addError("Data Source (Table) is required");
            return;
        }

        // Type specific validation
        if (newWidget.getType() == DashboardWidgetEntity.WidgetType.CHART) {
            if (newWidget.getXAxisField() == null) {
                addError("Group By (X-Axis) is required for Charts");
                return;
            }
            if (newWidget.getYAxisField() == null) {
                addError("Value Field (Y-Axis) is required");
                return;
            }
        } else if (newWidget.getType() == DashboardWidgetEntity.WidgetType.TABLE) {
            if (selectedTableColumns == null || selectedTableColumns.isEmpty()) {
                addError("At least one column must be selected for Table");
                return;
            }
            // Convert List to CSV
            newWidget.setTableColumns(String.join(",", selectedTableColumns));
        } else if (newWidget.getType() == DashboardWidgetEntity.WidgetType.CALENDAR) {
            if (newWidget.getXAxisField() == null) {
                addError("Date Column is required for Calendar");
                return;
            }
            if (newWidget.getYAxisField() == null) {
                addError("Event Title Column is required for Calendar");
                return;
            }
        } else if (newWidget.getType() == DashboardWidgetEntity.WidgetType.CARD) {
            if (newWidget.getYAxisField() == null) {
                addError("Value Field is required for Card");
                return;
            }
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (!isEditMode) {
                newWidget.setLayout(currentLayout);
                session.persist(newWidget);
                currentLayout.getWidgets().add(newWidget);
            } else {
                session.merge(newWidget);
                int idx = -1;
                for(int i=0; i<currentLayout.getWidgets().size(); i++) {
                    if(currentLayout.getWidgets().get(i).getId().equals(newWidget.getId())) {
                        idx = i;
                        break;
                    }
                }
                if(idx != -1) currentLayout.getWidgets().set(idx, newWidget);
            }
            tx.commit();
            loadChartData();
            PrimeFaces.current().executeScript("PF('widgetDialog').hide()");
            addInfo("Widget Saved");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            addError("Failed to save widget: " + e.getMessage());
        }
    }

    public void loadChartData() {
        Gson gson = new Gson();
        int index = 0;
        if (currentLayout == null || currentLayout.getWidgets() == null) return;

        for (DashboardWidgetEntity widget : currentLayout.getWidgets()) {
            String widgetId = "widget_" + index++;
            try {
                if (widget.getType() == DashboardWidgetEntity.WidgetType.CHART) {
                    Map<Object, Object> data = (Map<Object, Object>) queryService.executeWidgetQuery(widget);
                    String labels = gson.toJson(data.keySet());
                    String values = gson.toJson(data.values());
                    PrimeFaces.current().executeScript(String.format("renderDynamicChart('%s', '%s', %s, %s)",
                            widgetId, widget.getChartType(), labels, values));
                }
                else if (widget.getType() == DashboardWidgetEntity.WidgetType.CARD) {
                    Object value = queryService.executeWidgetQuery(widget);
                    String formattedValue = formatNumber(value);
                    PrimeFaces.current().executeScript(String.format("updateCardValue('%s', '%s')", widgetId, formattedValue));
                }
                else if (widget.getType() == DashboardWidgetEntity.WidgetType.TABLE) {
                    Map<String, Object> data = (Map<String, Object>) queryService.executeWidgetQuery(widget);
                    String jsonData = gson.toJson(data);
                    PrimeFaces.current().executeScript(String.format("renderDynamicTable('%s', %s)", widgetId, jsonData));
                }
                else if (widget.getType() == DashboardWidgetEntity.WidgetType.CALENDAR) {
                    List<Map<String, String>> events = (List<Map<String, String>>) queryService.executeWidgetQuery(widget);
                    String jsonEvents = gson.toJson(events);
                    PrimeFaces.current().executeScript(String.format("renderDynamicCalendar('%s', %s)", widgetId, jsonEvents));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleReorder() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String widgetOrder = params.get("widgetOrder"); // Comma separated IDs e.g. "5,2,8"

        if (widgetOrder == null || widgetOrder.isEmpty()) return;

        String[] ids = widgetOrder.split(",");
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            for (int i = 0; i < ids.length; i++) {
                Long id = Long.valueOf(ids[i]);
                DashboardWidgetEntity widget = session.get(DashboardWidgetEntity.class, id);
                if (widget != null) {
                    widget.setPosition(i);
                    session.merge(widget);
                }
            }

            tx.commit();
            // Reload layout to reflect order
            currentLayout = session.get(DashboardLayout.class, currentLayout.getId());
            addInfo("Dashboard Layout Updated");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    private String formatNumber(Object value) {
        if (value instanceof Number) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            return nf.format(value);
        }
        return value != null ? value.toString() : "0";
    }

    // Friendly Aggregation Names
    public List<SelectItem> getAggregationSelectItems() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem(DashboardWidgetEntity.AggregationType.COUNT, "Count (Total Items)"));
        items.add(new SelectItem(DashboardWidgetEntity.AggregationType.SUM, "Sum (Total Value)"));
        items.add(new SelectItem(DashboardWidgetEntity.AggregationType.AVG, "Average"));
        items.add(new SelectItem(DashboardWidgetEntity.AggregationType.MIN, "Minimum"));
        items.add(new SelectItem(DashboardWidgetEntity.AggregationType.MAX, "Maximum"));
        return items;
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }
    private void addInfo(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", msg));
    }

    // Getters
    public DashboardLayout getCurrentLayout() { return currentLayout; }
    public List<DashboardLayout> getAvailableLayouts() { return availableLayouts; }
    public Long getCurrentLayoutId() { return currentLayoutId; }
    public void setCurrentLayoutId(Long currentLayoutId) { this.currentLayoutId = currentLayoutId; }
    public DashboardWidgetEntity getNewWidget() { return newWidget; }
    public List<String> getAvailableTables() { return availableTables; }
    public List<String> getAvailableFields() { return availableFields; }
    public DashboardWidgetEntity.WidgetType[] getWidgetTypes() { return DashboardWidgetEntity.WidgetType.values(); }
    public DashboardWidgetEntity.ChartType[] getChartTypes() { return DashboardWidgetEntity.ChartType.values(); }
    public DashboardWidgetEntity.AggregationType[] getAggregationTypes() { return DashboardWidgetEntity.AggregationType.values(); }
    public String getNewDashboardName() { return newDashboardName; }
    public void setNewDashboardName(String newDashboardName) { this.newDashboardName = newDashboardName; }
    public List<String> getSelectedTableColumns() { return selectedTableColumns; }
    public void setSelectedTableColumns(List<String> selectedTableColumns) { this.selectedTableColumns = selectedTableColumns; }

    // Preview Getters
    public String getPreviewHql() { return previewHql; }
    public List<Map<String, Object>> getPreviewDataRows() { return previewDataRows; }
    public List<String> getPreviewDataColumns() { return previewDataColumns; }
}