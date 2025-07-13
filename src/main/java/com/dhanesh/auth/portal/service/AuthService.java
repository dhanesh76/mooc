package com.dhanesh.auth.portal.service;

import java.time.Instant;
import java.util.Map;

import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.auth.*;
import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.exception.AuthenticationFailedException;
import com.dhanesh.auth.portal.exception.EmailAlreadyInUseException;
import com.dhanesh.auth.portal.exception.UsernameAlreadyTakenException;
import com.dhanesh.auth.portal.model.AuthProvider;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.repository.UserRepository;
import com.dhanesh.auth.portal.security.jwt.JwtService;
import com.dhanesh.auth.portal.service.Redis.RedisAuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Handles user authentication and registration logic.
 * Integrates with Redis for temp user storage and OTP validation.
 * Issues JWT tokens for session management.
 */

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final RedisAuthService redisAuthService;

    /**
     * Initiates user registration by validating uniqueness and storing data in Redis.
     */
    public SignupResponse signup(SignupRequest credentials) {
        String username = credentials.username();
        String email = credentials.email();

        // Check if email/username already exists (DB or Redis cache)
        if (userRepo.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email already taken.");
        }

        Optional<SignupTempData> existingSignup = redisAuthService.getSignupData(email);
        if (existingSignup.isPresent()) {            
            return new SignupResponse(
                existingSignup.get().username(),
                email,
                "You already started registration. Please verify OTP sent to your email.",
                AuthProvider.LOCAL
                );
        }


        if (userRepo.findByUsername(username).isPresent() || redisAuthService.existsByUsername(username)) {
            throw new UsernameAlreadyTakenException("Username already taken.");
        }

        // Store temp user data in Redis
        String encodedPassword = passwordEncoder.encode(credentials.password());
        SignupTempData signupData = new SignupTempData(username, email, encodedPassword);
        redisAuthService.storeSignupData(email, signupData);

        // Send OTP for verification
        otpService.sendOtp(new OtpRequest(email, OtpPurpose.VERIFICATION));

        return new SignupResponse(
                username,
                email,
                "OTP sent to your email. Please verify to complete registration.",
                AuthProvider.LOCAL
        );
    }

    /**
     * Authenticates a user using username/email and password.
     */
    public SigninResponse signin(SigninRequest request) {
        Users user = userRepo.findByUsernameOrEmail(request.loginId(), request.loginId())
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));

        if (!auth.isAuthenticated()) {
            throw new AuthenticationFailedException("Authentication failed");
        }

        String token = jwtService.generateToken(user);

        return new SigninResponse(
                user.getId(),
                request.loginId(),
                user.getRole(),
                token,
                jwtService.extractExpiration(token),
                user.getAuthProvider()
        );
    }

    /**
     * Saves a verified user from Redis to DB after OTP verification.
     */
    public void saveNewUser(String email) {
        SignupTempData tempData = redisAuthService.getSignupData(email)
                .orElseThrow(() -> new RuntimeException("Session expired or not found"));

        Users user = new Users();
        user.setUsername(tempData.username());
        user.setEmail(tempData.email());
        user.setPassword(tempData.encodedPassword());
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setVerified(true);
        user.setRole("USER");
        user.setCreatedAt(Instant.now());

        userRepo.save(user);
        redisAuthService.deleteSignupData(email, tempData.username());
    }

    /**
     * Checks whether a registration session exists in Redis.
     */
    public boolean isRegisterSessionValid(String email) {
        return redisAuthService.hasKey("signup:" + email);
    }

    /**
     * Verifies reset token's validity for OTP-based password reset.
     */
    public Map<String, Object> validatePasswordResetToken(ResetPasswordRequest request, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Map.of("valid", false, "message", "Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7);

        if(!jwtService.extractLoginId(token).equals(request.email())){
            return Map.of("valid", false, "message", "Email Mismatch");
        }

        if (jwtService.isTokenExpired(token)) {
            return Map.of("valid", false, "message", "Token expired.");
        }

        Boolean isVerified = jwtService.extractClaim(token, claims -> claims.get("otp_verified", Boolean.class));
        String type = jwtService.extractClaim(token, claims -> claims.get("token_type", String.class));


        if (!Boolean.TRUE.equals(isVerified) || !"otp".equals(type)) {
            return Map.of("valid", false, "message", "Invalid or unauthorized token.");
        }

        return Map.of("valid", true, "message", "Token verified.");
    }

    /**
     * Resets password after OTP verification.
     */
    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationFailedException("User not found"));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepo.save(user);
    }

    public boolean emailExists(String email){
        return userRepo.existsByEmail(email);
    }

    public String getClientIp(HttpServletRequest servletRequest) {
        String header = servletRequest.getHeader("X-Forwarded-For");

        if(header != null)
            return header.split(", ")[0];
        
        return servletRequest.getRemoteAddr();
    }
}
