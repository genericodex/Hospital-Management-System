package com.pahappa.filter;

import com.pahappa.util.HibernateUtil;
import jakarta.servlet.*;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

        /**
         * <p>
         * The doFilter() method is the most important method in this filter.
         * <p>
         *   It's called by the web server for every single
         *   request that matches the filter's mapping in the
         *   web.xml configuration file.
         *   <p>
         *    - request: Contains all the information about
         *      the incoming request from the user's browser.<p>
         *    - response: Used to build the response that will
         *      be sent back to the user.<p>
         *    - chain: Represents the chain of filters. You
         *      call chain.doFilter() to pass the request
         *      along to the next filter or, if there are no
         *      more filters, to the servlet/JSF page itself.
         */
public class OpenSessionInViewFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(OpenSessionInViewFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            logger.debug("Hibernate session opened and transaction begun.");

            chain.doFilter(request, response);
            session.getTransaction().commit();
            logger.debug("Transaction committed successfully.");

        } catch (Exception e) {
            logger.error("Exception in OpenSessionInViewFilter, rolling back transaction.", e);
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
                logger.debug("Transaction rolled back.");
            }
            throw new ServletException(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
                logger.debug("Hibernate session closed.");
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("OpenSessionInViewFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("OpenSessionInViewFilter destroyed");
    }
}
