package com.pahappa.services.dashboard;

import com.pahappa.models.analytics.AnalysisDimension;
import com.pahappa.models.analytics.BenchmarkLog;
import com.pahappa.models.analytics.CurrentView;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DashboardScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DashboardScheduler.class);
    private ScheduledExecutorService scheduler;

    // Start the scheduler when the application scope is initialized
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        logger.info("Initializing Dashboard Scheduler (CDI)...");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Run every 1 minute
        scheduler.scheduleAtFixedRate(this::computeAnalytics, 0, 1, TimeUnit.MINUTES);
    }

    // Stop the scheduler when the application shuts down
    public void destroy(@Observes @jakarta.enterprise.context.Destroyed(ApplicationScoped.class) Object init) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    // Public method to allow manual triggering from the UI
    public void forceRun() {
        logger.info("Manual analysis run triggered.");
        computeAnalytics();
    }

    private void computeAnalytics() {
        logger.debug("Starting analytics computation job.");
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<AnalysisDimension> dimensions = session.createQuery("FROM AnalysisDimension", AnalysisDimension.class).list();

            for (AnalysisDimension dim : dimensions) {
                processDimension(session, dim);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error during analytics computation", e);
        }
    }

    private void processDimension(Session session, AnalysisDimension dim) {
        try {
            CurrentView currentView = session.createQuery(
                            "FROM CurrentView cv WHERE cv.analysisDimension.id = :dimId", CurrentView.class)
                    .setParameter("dimId", dim.getId())
                    .uniqueResult();

            boolean needsUpdate = false;
            if (currentView == null) {
                needsUpdate = true;
            } else {
                long minutesSinceLastUpdate = ChronoUnit.MINUTES.between(currentView.getComputationDate(), LocalDateTime.now());
                if (minutesSinceLastUpdate >= dim.getFrequency()) {
                    needsUpdate = true;
                }
            }

            if (needsUpdate) {
                String hql = dim.getDataPoint().getQuery();
                Object resultObj = session.createQuery(hql, Object.class).uniqueResult();

                Double newResultValue = 0.0;
                if (resultObj instanceof Number) {
                    newResultValue = ((Number) resultObj).doubleValue();
                }

                LocalDateTime now = LocalDateTime.now();

                if (currentView != null) {
                    // Archive OLD data
                    BenchmarkLog log = new BenchmarkLog();
                    log.setAnalysisDimension(dim);
                    log.setResult(currentView.getResult());
                    log.setComputationDate(currentView.getComputationDate());
                    log.setStatus(currentView.getStatus());
                    session.persist(log);

                    // Update CurrentView with NEW data
                    currentView.setResult(newResultValue);
                    currentView.setComputationDate(now);
                    currentView.setStatus("Active");
                    session.merge(currentView);

                    logger.info("Updated Dimension '{}': Old={} -> New={}", dim.getName(), log.getResult(), newResultValue);
                } else {
                    // First time creation
                    currentView = new CurrentView();
                    currentView.setAnalysisDimension(dim);
                    currentView.setResult(newResultValue);
                    currentView.setComputationDate(now);
                    currentView.setStatus("Active");
                    session.persist(currentView);

                    logger.info("Created Dimension '{}': Result={}", dim.getName(), newResultValue);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process dimension: " + dim.getName(), e);
        }
    }
}