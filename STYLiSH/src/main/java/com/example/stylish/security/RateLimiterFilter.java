package com.example.stylish.security;

import com.example.stylish.service.GetClientIP;
import com.example.stylish.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class RateLimiterFilter extends OncePerRequestFilter {
    private final RateLimiterService rateLimiterService;
    private final GetClientIP getClientIP;

    public RateLimiterFilter(RateLimiterService rateLimiterService, GetClientIP getClientIP) {
        this.rateLimiterService = rateLimiterService;
        this.getClientIP = getClientIP;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String ip = getClientIP.getIP(request);
        boolean allowed = rateLimiterService.isAllowed(ip);

        if (!allowed) {
            response.setStatus(429);
            return;
        }
        filterChain.doFilter(request, response);
    }

}

