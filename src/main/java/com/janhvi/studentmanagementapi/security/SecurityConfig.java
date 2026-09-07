package com.janhvi.studentmanagementapi.security;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // ========================================
    // CONSTRUCTOR
    // ========================================

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    // ========================================
    // PASSWORD ENCODER
    // ========================================

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ========================================
    // SECURITY FILTER CHAIN
    // ========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http


                // ========================================
                // SECURITY ERROR DEBUG
                // ========================================

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    System.out.println(
                                            "\n===================================="
                                    );

                                    System.out.println(
                                            "SECURITY ERROR"
                                    );

                                    System.out.println(
                                            "URI : "
                                                    + request.getRequestURI()
                                    );

                                    System.out.println(
                                            "METHOD : "
                                                    + request.getMethod()
                                    );

                                    System.out.println(
                                            "ERROR : "
                                                    + authException.getMessage()
                                    );

                                    System.out.println(
                                            "====================================\n"
                                    );

                                    response.sendError(
                                            HttpServletResponse.SC_FORBIDDEN,
                                            authException.getMessage()
                                    );
                                }
                        )
                )


                // ========================================
                // CORS
                // ========================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )


                // ========================================
                // CSRF DISABLED
                // ========================================

                .csrf(csrf ->
                        csrf.disable()
                )


                // ========================================
                // JWT STATELESS SESSION
                // ========================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ========================================
                // URL SECURITY
                // ========================================

                .authorizeHttpRequests(auth -> auth


                        // ========================================
                        // OPTIONS
                        // ========================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // ========================================
                        // CHROME DEVTOOLS REQUEST
                        // ========================================

                        .requestMatchers(
                                "/.well-known/**"
                        ).permitAll()


                        // ========================================
                        // PUBLIC
                        // ========================================

                        .requestMatchers(

                                "/auth/**",
                                "/error",
                                "/admin/create-first",


                                // PUBLIC PAGES

                                "/",
                                "/home.html",
                                "/login.html",
                                "/register.html",
                                "/forgot-password.html",
                                "/dashboard.html",
                                "/student-dashboard.html",
                                "/admin-management.html",


                                // STATIC FILES

                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/favicon.ico",


                                // PROFILE

                                "/profile/**"

                        ).permitAll()


                        // ========================================
                        // AUDIT LOGS
                        //
                        // ONLY ADMIN CAN VIEW AUDIT LOGS
                        // ========================================

                        .requestMatchers(
                                "/audit-logs/**"
                        ).hasRole("ADMIN")


                        // ========================================
                        // STUDENT POST
                        // ========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/students"
                        ).permitAll()


                        // ========================================
                        // STUDENT GET
                        // ========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/students/**"
                        ).permitAll()


                        // ========================================
                        // STUDENT PUT
                        // ========================================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/students/**"
                        ).permitAll()


                        // ========================================
                        // STUDENT DELETE
                        // ========================================

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/students/**"
                        ).permitAll()


                        // ========================================
                        // ADMIN
                        // ========================================

                        .requestMatchers(
                                "/admin/**"
                        ).permitAll()


                        // ========================================
                        // PROFILE
                        // ========================================

                        .requestMatchers(
                                "/profile/**"
                        ).permitAll()


                        // ========================================
                        // OTHER REQUESTS
                        // ========================================

                        .anyRequest()
                        .authenticated()
                )


                // ========================================
                // JWT FILTER
                // ========================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


    // ========================================
    // CORS CONFIGURATION
    // ========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOriginPatterns(
                List.of("*")
        );


        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );


        configuration.setAllowedHeaders(
                List.of("*")
        );


        configuration.setAllowCredentials(false);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}