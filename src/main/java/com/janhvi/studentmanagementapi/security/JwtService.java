package com.janhvi.studentmanagementapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {


    private final String SECRET_KEY =
            "mySecretKeyForStudentManagementApiProject2025";


    private final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24; // 24 Hours


    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }


    // ========================================
    // GENERATE TOKEN
    // ========================================

   public String generateToken(
        Long userId,
        String username,
        String role) {

        return Jwts.builder()

                .subject(username)

                .claim(
        "userId",
        userId
)

                .claim(
                        "role",
                        role
                )

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )

                .signWith(
                        getSigningKey()
                )

                .compact();
    }


    // ========================================
    // EXTRACT USERNAME
    // ========================================

    public String extractUsername(
            String token) {

        return Jwts.parser()

                .verifyWith(
                        getSigningKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload()

                .getSubject();
    }

    // ========================================
// EXTRACT USER ID
// ========================================

public Long extractUserId(
        String token) {

    Object userId =
            Jwts.parser()

                    .verifyWith(
                            getSigningKey()
                    )

                    .build()

                    .parseSignedClaims(token)

                    .getPayload()

                    .get("userId");

    if (userId == null) {

        return null;

    }

    return ((Number) userId).longValue();
}

    // ========================================
    // EXTRACT ROLE
    // ========================================

    public String extractRole(
            String token) {

        return (String)
                Jwts.parser()

                        .verifyWith(
                                getSigningKey()
                        )

                        .build()

                        .parseSignedClaims(token)

                        .getPayload()

                        .get("role");
    }


    // ========================================
    // VALIDATE TOKEN
    // ========================================

    public boolean isTokenValid(
            String token) {

        try {

            Jwts.parser()

                    .verifyWith(
                            getSigningKey()
                    )

                    .build()

                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

}