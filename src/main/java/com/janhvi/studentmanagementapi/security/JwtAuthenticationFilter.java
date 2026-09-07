package com.janhvi.studentmanagementapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;


    public JwtAuthenticationFilter(
            JwtService jwtService) {

        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(

            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain

    ) throws ServletException, IOException {


        // ========================================
        // JWT FILTER DEBUG
        // ========================================

        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "JWT FILTER START"
        );

        System.out.println(
                "URI       : "
                        + request.getRequestURI()
        );

        System.out.println(
                "METHOD    : "
                        + request.getMethod()
        );


        String requestPath =
                request.getRequestURI();


        // ========================================
        // SKIP JWT FOR PUBLIC PAGES
        // ========================================

        if (

                requestPath.equals("/") ||

                requestPath.equals("/home.html") ||

                requestPath.equals("/login.html") ||

                requestPath.equals("/register.html") ||

                requestPath.equals("/forgot-password.html") ||

                requestPath.equals("/dashboard.html") ||

                requestPath.equals("/student-dashboard.html") ||

                requestPath.equals("/admin-management.html") ||

                requestPath.startsWith("/css/") ||

                requestPath.startsWith("/js/") ||

                requestPath.startsWith("/images/") ||

                requestPath.startsWith("/uploads/") ||

                requestPath.startsWith("/auth/") ||

                requestPath.equals("/favicon.ico")

        ) {


            System.out.println(
                    "PUBLIC PAGE - JWT FILTER SKIPPED"
            );

            System.out.println(
                    "========================================\n"
            );


            filterChain.doFilter(
                    request,
                    response
            );


            return;
        }


        // ========================================
        // GET AUTHORIZATION HEADER
        // ========================================

        String authHeader =
                request.getHeader(
                        "Authorization"
                );


        System.out.println(
                "AUTH HEADER : "
                        + authHeader
        );


        // ========================================
        // NO JWT TOKEN
        // ========================================

        if (

                authHeader == null ||

                !authHeader.startsWith(
                        "Bearer "
                )

        ) {


            System.out.println(
                    "NO JWT TOKEN FOUND"
            );

            System.out.println(
                    "========================================\n"
            );


            filterChain.doFilter(
                    request,
                    response
            );


            return;
        }


        // ========================================
        // EXTRACT TOKEN
        // ========================================

        String token =
                authHeader.substring(
                        7
                );


        System.out.println(
                "TOKEN : "
                        + token
        );


        try {


            // ========================================
            // VALIDATE TOKEN
            // ========================================

            if (

                    jwtService.isTokenValid(
                            token
                    )

            ) {


                // ========================================
                // EXTRACT JWT DATA
                // ========================================

                String username =
                        jwtService.extractUsername(
                                token
                        );


                Long userId =
                        jwtService.extractUserId(
                                token
                        );


                String role =
                        jwtService.extractRole(
                                token
                        );


                System.out.println(
                        "USERNAME : "
                                + username
                );


                System.out.println(
                        "USER ID  : "
                                + userId
                );


                System.out.println(
                        "ROLE     : "
                                + role
                );


                // ========================================
                // CREATE AUTHENTICATION
                // ========================================

                UsernamePasswordAuthenticationToken
                        authentication =

                        new UsernamePasswordAuthenticationToken(

                                userId,

                                null,

                                List.of(

                                        new SimpleGrantedAuthority(

                                                "ROLE_" + role

                                        )

                                )

                        );


                // ========================================
                // SET SECURITY CONTEXT
                // ========================================

                SecurityContextHolder

                        .getContext()

                        .setAuthentication(

                                authentication

                        );


                System.out.println(
                        "AUTHENTICATION SUCCESS"
                );


                System.out.println(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                );


                // ========================================
                // AUDIT DEBUG
                // ========================================

                System.out.println(
                        "\n========================================"
                );


                System.out.println(
                        "AUDIT USER ID FROM SECURITY CONTEXT : "
                                +
                                SecurityContextHolder
                                        .getContext()
                                        .getAuthentication()
                                        .getPrincipal()
                );


                System.out.println(
                        "AUDIT ROLE : "
                                +
                                SecurityContextHolder
                                        .getContext()
                                        .getAuthentication()
                                        .getAuthorities()
                );


                System.out.println(
                        "========================================"
                );


            } else {


                System.out.println(
                        "INVALID JWT TOKEN"
                );


            }


        } catch (Exception e) {


            System.out.println(
                    "JWT EXCEPTION"
            );


            e.printStackTrace();


        }


        // ========================================
        // JWT FILTER END
        // ========================================

        System.out.println(
                "JWT FILTER END"
        );


        System.out.println(
                "========================================\n"
        );


        filterChain.doFilter(
                request,
                response
        );

    }

}