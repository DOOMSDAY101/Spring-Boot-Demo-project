package com.example.demo.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OtpActionTokenFilter extends OncePerRequestFilter {

    private JWTService jwtService;

    @Autowired
    public OtpActionTokenFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        boolean requiresActionToken = ("POST".equalsIgnoreCase(method) && uri.endsWith("/product"))
                || ("PUT".equalsIgnoreCase(method) && uri.matches(".*/product/\\d+$"));

        if (!requiresActionToken) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated");
            return;
        }

        String otpToken = request.getHeader("X-OTP-Token");
        if (otpToken == null || otpToken.isBlank()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing X-OTP-Token header");
            return;
        }

        boolean valid = jwtService.validateActionToken(otpToken, auth.getName());
        if (!valid) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired OTP token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}