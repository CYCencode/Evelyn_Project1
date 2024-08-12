package com.example.stylish.controller;

import com.example.stylish.service.ErrorWrapper;
import com.example.stylish.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final JwtTokenProvider jwtTokenProvider;
    public UserController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String jwt = (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null;
            if (jwt == null) {
                log.warn("未提供Token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorWrapper.wrapErrorMsg("Token not found in header."));
            }

            if (!jwtTokenProvider.validateToken(jwt)) {
                log.warn("Token 格式錯誤或無效: {}", token);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ErrorWrapper.wrapErrorMsg("Invalid jwt token."));
            }
            Map<String, Object> claims = jwtTokenProvider.getAllClaimsFromToken(jwt);
            Map<String, Object> profileResponse = new HashMap<>();
            profileResponse.put("provider", claims.get("provider"));
            profileResponse.put("name", claims.get("name"));
            profileResponse.put("email", claims.get("email"));
            profileResponse.put("picture", claims.get("picture"));
            Map<String, Object> response = new HashMap<>();
            response.put("data", profileResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("伺服器錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorWrapper.wrapErrorMsg("Server error occurred while loading profile."));
        }
    }

}