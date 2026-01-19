package com.pahappa.services.analytics;

import com.pahappa.models.analytics.*;
import com.pahappa.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@ApplicationScoped
public class AnalyticsService {

    public void saveDataPoint(DataPoint dataPoint) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(dataPoint);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<DataPoint> getAllDataPoints() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DataPoint", DataPoint.class).list();
        }
    }

    public void deleteDataPoint(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DataPoint dp = session.get(DataPoint.class, id);
            if (dp != null) session.remove(dp);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void saveAnalysisDimension(AnalysisDimension dimension) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(dimension);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<AnalysisDimension> getAllAnalysisDimensions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM AnalysisDimension", AnalysisDimension.class).list();
        }
    }

    public void deleteAnalysisDimension(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            AnalysisDimension ad = session.get(AnalysisDimension.class, id);
            if (ad != null) session.remove(ad);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<CurrentView> getAllCurrentViews() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM CurrentView ORDER BY computationDate DESC", CurrentView.class).list();
        }
    }

    public List<BenchmarkLog> getRecentBenchmarkLogs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Fetch last 100 logs to avoid overwhelming the UI
            return session.createQuery("FROM BenchmarkLog ORDER BY computationDate DESC", BenchmarkLog.class)
                    .setMaxResults(100)
                    .list();
        }
    }
}