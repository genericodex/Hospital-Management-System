package com.pahappa.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// It's good practice to use the @WebFilter annotation
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // 1. Check if the requested page is public (login page or a resource like CSS/JS)
        // This prevents an infinite redirect loop.
        boolean isPublicResource = uri.contains("/Auth/login.xhtml") || uri.contains("/jakarta.faces.resource/");

        if (isPublicResource) {
            // If it's a public page, let the request continue without checking for a session.
            chain.doFilter(request, response);
            return;
        }

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        res.setDateHeader("Expires", 0); // Proxies.

        // 2. For all other protected pages, check if a user is logged in.
        HttpSession session = req.getSession(false); // 'false' means don't create a new session if one doesn't exist.

        boolean isLoggedIn = (session != null) &&
                (session.getAttribute("authBean") != null || session.getAttribute("doctorAuthBean") != null);

        if (isLoggedIn) {
            // User is authenticated, so allow the request to proceed to the requested page.
            chain.doFilter(request, response);
        } else {
            // User is not authenticated, redirect them to the login page.
            res.sendRedirect(req.getContextPath() + "/Auth/login.xhtml");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code can go here if needed.
    }

    @Override
    public void destroy() {
        // Cleanup code can go here if needed.
    }
}