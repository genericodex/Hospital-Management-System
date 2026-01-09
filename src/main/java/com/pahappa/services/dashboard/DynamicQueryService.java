package com.pahappa.services.dashboard;

import com.pahappa.models.analytics.DashboardWidgetEntity;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class DynamicQueryService {

    public Object executeWidgetQuery(DashboardWidgetEntity widget) {
        if (widget.getType() == DashboardWidgetEntity.WidgetType.CARD) {
            return executeCardQuery(widget);
        } else if (widget.getType() == DashboardWidgetEntity.WidgetType.CHART) {
            return executeChartQuery(widget);
        } else if (widget.getType() == DashboardWidgetEntity.WidgetType.TABLE) {
            return executeTableQuery(widget);
        } else if (widget.getType() == DashboardWidgetEntity.WidgetType.CALENDAR) {
            return executeCalendarQuery(widget);
        }
        return null;
    }

    private Object executeCardQuery(DashboardWidgetEntity widget) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String entity = widget.getDataSourceTable();
            String agg = widget.getAggregation().name();
            String field = widget.getYAxisField();

            if (entity == null || field == null) return 0;

            String selectField = "e." + field;
            String hql = String.format("SELECT %s(%s) FROM %s e WHERE e.isDeleted = false", agg, selectField, entity);

            if (widget.getFilterClause() != null && !widget.getFilterClause().trim().isEmpty()) {
                hql += " WHERE " + widget.getFilterClause();
            }

            Object result = session.createQuery(hql, Object.class).uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("Error executing Card Query: " + e.getMessage());
            return 0;
        }
    }

    private Map<Object, Object> executeChartQuery(DashboardWidgetEntity widget) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String entity = widget.getDataSourceTable();
            String groupField = widget.getXAxisField();
            String valField = widget.getYAxisField();
            String agg = widget.getAggregation().name();

            // Safety check
            if (entity == null || groupField == null || valField == null) {
                System.err.println("Missing configuration for chart: " + widget.getTitle());
                return Collections.emptyMap();
            }

            String selectGroup = "e." + groupField;
            String selectVal = "e." + valField;

            if (groupField.toLowerCase().contains("date") || groupField.toLowerCase().contains("time")) {
                selectGroup = "cast(" + selectGroup + " as date)";
            }

            StringBuilder hqlBuilder = new StringBuilder();
            hqlBuilder.append(String.format("SELECT %s, %s(%s) FROM %s e WHERE e.isDeleted = false",
                    selectGroup, agg, selectVal, entity));

            if (widget.getFilterClause() != null && !widget.getFilterClause().trim().isEmpty()) {
                hqlBuilder.append(" AND (").append(widget.getFilterClause()).append(")");
            }

            hqlBuilder.append(" GROUP BY ").append(selectGroup);


            List<Object[]> results = session.createQuery(hqlBuilder.toString(), Object[].class).list();

            // Use merge function (v1, v2) -> v1 to handle potential duplicate keys safely
            return results.stream().collect(Collectors.toMap(
                    row -> row[0] != null ? row[0].toString() : "Unknown",
                    row -> row[1] != null ? row[1] : 0,
                    (v1, v2) -> v1
            ));
        } catch (Exception e) {
            System.err.println("Error executing Chart Query: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private Map<String, Object> executeTableQuery(DashboardWidgetEntity widget) {
        Map<String, Object> result = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String entity = widget.getDataSourceTable();
            String columnsStr = widget.getTableColumns();

            // --- FIX: Added logging to debug missing columns ---
            System.out.println("Executing Table Query for Widget: " + widget.getTitle());
            System.out.println("Entity: " + entity);
            System.out.println("Columns: " + columnsStr);

            if (entity == null || columnsStr == null || columnsStr.isEmpty()) {
                System.err.println("Table configuration missing (columns are empty).");
                return result;
            }

            String[] columns = columnsStr.split(",");
            StringBuilder selectClause = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) selectClause.append(", ");
                selectClause.append("e.").append(columns[i].trim());
            }

            String hql = String.format("SELECT %s FROM %s e WHERE e.isDeleted = false", selectClause, entity);

            if (widget.getFilterClause() != null && !widget.getFilterClause().trim().isEmpty()) {
                hql += " AND ( " + widget.getFilterClause() + " )";
            }

            // Limit rows to prevent browser crash
            List<?> rawResults;
            if (columns.length > 1) {
                // For multiple columns, Hibernate returns Object[]
                rawResults = session.createQuery(hql, Object[].class).setMaxResults(50).list();
            } else {
                // For a single column, Hibernate returns Object
                rawResults = session.createQuery(hql, Object.class).setMaxResults(50).list();
            }

            List<List<String>> rows = new ArrayList<>();
            for (Object obj : rawResults) {
                List<String> row = new ArrayList<>();
                if (obj instanceof Object[]) {
                    for (Object col : (Object[]) obj) {
                        row.add(col != null ? col.toString() : "");
                    }
                } else {
                    row.add(obj != null ? obj.toString() : "");
                }
                rows.add(row);
            }

            result.put("headers", Arrays.asList(columns));
            result.put("rows", rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Map<String, String>> executeCalendarQuery(DashboardWidgetEntity widget) {
        List<Map<String, String>> events = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String entity = widget.getDataSourceTable();
            String dateField = widget.getXAxisField(); // Reusing X-Axis for Date
            String titleField = widget.getYAxisField(); // Reusing Y-Axis for Title

            if (entity == null || dateField == null || titleField == null) {
                return events;
            }

            String hql = String.format("SELECT e.%s, e.%s FROM %s e WHERE e.isDeleted = false", dateField, titleField, entity);

            if (widget.getFilterClause() != null && !widget.getFilterClause().trim().isEmpty()) {
                hql += " AND ( " + widget.getFilterClause() + " )" ;
            }

            List<Object[]> results = session.createQuery(hql, Object[].class).setMaxResults(100).list();

            for (Object[] row : results) {
                if (row[0] != null) {
                    Map<String, String> event = new HashMap<>();
                    event.put("start", row[0].toString()); // ISO date string usually works
                    event.put("title", row[1] != null ? row[1].toString() : "Event");
                    events.add(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
