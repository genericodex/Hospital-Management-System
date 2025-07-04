package com.pahappa.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Allow access to login page and resources without authentication
        String uri = req.getRequestURI();
        boolean loginPage = uri.contains("login.xhtml");
        boolean resource = uri.contains("/resources/");

        // Only check session for protected pages
        if (!loginPage && !resource) {
<<<<<<< Updated upstream
            Object auth = req.getSession().getAttribute("authBean");
            if (auth == null) {
=======
<<<<<<< Updated upstream
            Object auth = req.getSession().getAttribute("authBean");
            if (auth == null) {
=======
            Object staffAuth = req.getSession().getAttribute("authBean");
            Object doctorAuth = req.getSession().getAttribute("doctorAuthBean");
            if (staffAuth == null && doctorAuth == null) {
>>>>>>> Stashed changes
>>>>>>> Stashed changes
                res.sendRedirect(req.getContextPath() + "/login.xhtml");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}