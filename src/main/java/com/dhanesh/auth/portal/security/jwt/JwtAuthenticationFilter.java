package com.dhanesh.auth.portal.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * Filter that intercepts every request to check for a valid JWT token.
 * If valid, it sets the authentication in the SecurityContext.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    @SuppressWarnings("")
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // If no token or doesn't start with "Bearer ", skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token by removing the "Bearer " prefix
        String token = authHeader.substring(7);

        // Extract username or email (loginId) from the token
        String loginId = jwtService.extractLoginId(token);

        // If token is malformed or already authenticated, skip further processing
        if (loginId == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Load user details from DB or memory using UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);

        // Validate token against the user details
        if (!jwtService.validateToken(token, userDetails)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Create an authentication token for the current request
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,  // no credentials required since token is valid
                        userDetails.getAuthorities()
                );

        // Attach request-specific details (like IP, session ID, etc.)
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authenticated user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
