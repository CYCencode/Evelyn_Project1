package com.example.stylish.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class GetClientIP {
    public String getIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        String uri = request.getRequestURI();
        if (xfHeader == null) {
            return request.getRemoteAddr() + ":" + uri;
        }
        return xfHeader.split(",")[0] + ":" + uri;
    }
}

