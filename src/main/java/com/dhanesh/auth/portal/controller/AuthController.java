package com.dhanesh.auth.portal.controller;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.dto.auth.ResetPasswordRequest;
import com.dhanesh.auth.portal.dto.auth.SigninRequest;
import com.dhanesh.auth.portal.dto.auth.SigninResponse;
import com.dhanesh.auth.portal.dto.auth.SignupRequest;
import com.dhanesh.auth.portal.dto.auth.SignupResponse;
import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.dto.otp.OtpValidationResult;
import com.dhanesh.auth.portal.dto.otp.OtpVerifyRequest;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.security.jwt.JwtService;
import com.dhanesh.auth.portal.service.AuthService;
import com.dhanesh.auth.portal.service.OtpService;
import com.dhanesh.auth.portal.service.Redis.RedisRateLimitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

/**
 * Controller responsible for user authentication and OTP flows.
 * Handles signup, signin, OTP request/verification, and password reset.
 */
@Tag(name = "Authentication", description = "Endpoints for user signup, signin, OTP, and password reset")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final RedisRateLimitService rateLimitService;

    /**
     * Registers a new user with email-based verification.
     */
    @Operation(summary = "User signup", description = "Registers a new user and sends OTP to their email.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
        @Valid @RequestBody 
        @Parameter(description = "Signup request body with email and password") 
        SignupRequest signupRequest
    ) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    @Operation(summary = "User signin", description = "Authenticates user with email and password and returns a JWT.")
    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(
        @Valid @RequestBody 
        @Parameter(description = "Signin request with email and password") 
        SigninRequest signinRequest
    ) {
        SigninResponse response = userService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifies the OTP for email verification or password reset.
     */
    @Operation(summary = "Verify OTP", description = "Verifies the OTP for either email verification or password reset.")
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(
        @RequestBody 
        @Parameter(description = "OTP verification request body") 
        OtpVerifyRequest request
    ) {
        OtpValidationResult otpValidation = otpService.validateOtp(request);

        if (!otpValidation.valid()) {
            return ResponseEntity.badRequest().body(Map.of(
                "verified", false,
                "message", otpValidation.message()
            ));
        }

        if (request.purpose() == OtpPurpose.VERIFICATION) {
            if (!userService.isRegisterSessionValid(request.email())) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(Map.of("message", "Session expired. Please register again."));
            }

            userService.saveNewUser(request.email());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully. Continue to login."));
        }

        // OTP for password reset
        String token = jwtService.generateOtpToken(request.email());
        return ResponseEntity.ok(Map.of(
            "message", "OTP verified successfully.",
            "token", token,
            "time-stamp", Instant.now()
        ));
    }

    /**
     * Sends an OTP to the user's email for verification or password reset.
     */
    @Operation(summary = "Request OTP", description = "Sends OTP for email verification or password reset.")
    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, Object>> requestOtp(
        @Valid @RequestBody 
        @Parameter(description = "OTP request with email and purpose") 
        OtpRequest request,

        @Parameter(hidden = true) HttpServletRequest servletRequest
    ) {
        String clientIp = userService.getClientIp(servletRequest);

        if (!rateLimitService.isAllowed(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("success", false, "message", "Too many OTP requests from this IP. Try again later.", "timestamp", Instant.now()));
        }

        if (rateLimitService.isInCooldown(clientIp)) {
            long cooldown = rateLimitService.getCooldownRemaining(clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                    "success", false,
                    "cooldown_remaining", cooldown,
                    "message", "Please wait " + cooldown + " seconds before requesting a new OTP."
                ));
        }

        if (request.purpose() == OtpPurpose.PASSWORD_RESET && !userService.emailExists(request.email())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Email not registered", "timestamp", Instant.now()));
        }

        if (request.purpose() == OtpPurpose.VERIFICATION && userService.emailExists(request.email())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "The email is already verified", "timestamp", Instant.now()));
        }

        if (otpService.sendOtp(request)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent successfully to your email", "timestamp", Instant.now()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Failed to send OTP. Please try again later.", "timestamp", Instant.now()));
        }
    }

    /**
     * Resets the user's password using a short-lived token obtained from OTP verification.
     */
    @Operation(summary = "Reset password", description = "Resets user's password after OTP validation using a short-lived JWT.")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody 
        @Parameter(description = "New password and confirmation") 
        ResetPasswordRequest request,

        @RequestHeader("Authorization") 
        @Parameter(description = "Short-lived JWT returned after OTP verification", required = true) 
        String authHeader
    ) {
        Map<String, Object> tokenValidation = userService.validatePasswordResetToken(request, authHeader);

        if (Boolean.FALSE.equals(tokenValidation.get("valid"))) {
            return ResponseEntity.badRequest().body((String) tokenValidation.get("message"));
        }

        userService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful. Please login with your new password.");
    }
}
