package com.example.stylish.config;

import com.example.stylish.security.JwtAuthenticationFilter;
import com.example.stylish.security.RateLimiterFilter;
import com.example.stylish.service.GetClientIP;
import com.example.stylish.service.RateLimiterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private RateLimiterService rateLimiterService;
    private GetClientIP getClientIP;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // allow no auth API
                        .requestMatchers("/", "login.html", "index.html", "product.html", "profile.html", "thankyou.html", "/rate_limiter/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/api/1.0/products/**").permitAll()
                        .requestMatchers("/api/1.0/order/**").permitAll()
                        .requestMatchers("/api/1.0/marketing/**").permitAll()
                        .requestMatchers("/static/**", "/img/**", "/style/**", "/script/**").permitAll()
                        // need auth API
                        .requestMatchers("/api/1.0/user/**").permitAll()
                        // other need auth
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Make sure JwtAuthenticationFilter inject correctly, for tokenProvider
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(rateLimiterFilter(rateLimiterService, getClientIP), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public RateLimiterFilter rateLimiterFilter(RateLimiterService rateLimiterService, GetClientIP getClientIP) {
        return new RateLimiterFilter(rateLimiterService, getClientIP);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
