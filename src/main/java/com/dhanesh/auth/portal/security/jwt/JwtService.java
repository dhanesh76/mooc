package com.dhanesh.auth.portal.security.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service for handling all JWT-related operations such as generation,
 * validation, and claim extraction.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration; // in milliseconds (e.g., 86400000 = 1 day)

    /**
     * Generates a JWT token for the given loginId (usually email or username).
     */
    public String generateToken(String loginId) {
        return Jwts.builder()
                .subject(loginId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), Jwts.SIG.HS256) // Sign with HMAC-SHA256
                .compact();
    }

    /**
     * Extracts the login identifier (subject) from a token.
     */
    public String extractLoginId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration time from a token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates the token:
     * - Checks if the subject matches the userDetails
     * - Ensures token is not expired
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String loginId = extractLoginId(token);
        return loginId.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the JWT token has expired.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generic method to extract any claim from the token using a resolver.
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = parseClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Parses and validates the JWT signature and returns the claims.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey()) // Verify the signature
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Extract the body (claims)
    }

    /**
     * Converts the Base64-encoded secret key into a usable HMAC key.
     */
    private SecretKey getKey() {
        byte[] decodedKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    /** 
     * Generates a short lived token for the opt verification  
     */
    public String generateOtpToken(String loginId) {
        return Jwts.builder()
                .subject(loginId)
                .claim("otp_verified", true)
                .claim("token_type", "otp")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*5))
                .signWith(getKey(), Jwts.SIG.HS256) // Sign with HMAC-SHA256
                .compact();
    }
}
