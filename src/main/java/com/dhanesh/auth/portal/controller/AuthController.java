package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.auth.ResetPasswordRequest;
import com.dhanesh.auth.portal.dto.auth.SigninRequest;
import com.dhanesh.auth.portal.dto.auth.SigninResponse;
import com.dhanesh.auth.portal.dto.auth.SignupRequest;
import com.dhanesh.auth.portal.dto.auth.SignupResponse;
import com.dhanesh.auth.portal.dto.otp.EmailOnlyRequest;
import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.dto.otp.OtpResponse;
import com.dhanesh.auth.portal.dto.otp.OtpVerifyRequest;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.security.jwt.JwtService;
import com.dhanesh.auth.portal.service.OtpService;
import com.dhanesh.auth.portal.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;
    private final OtpService otpService;
    private final JwtService jwtService;

    /**
     * Registers a new user and sends OTP for email verification.
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user using email or username and password.
     * If the user is not verified, an OTP is sent and exception is thrown.
     */
    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        SigninResponse response = userService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Validates OTP for either VERIFICATION or PASSWORD_RESET.
     * If VERIFICATION, marks user as verified.
     * If PASSWORD_RESET, front-end should redirect to /reset-password endpoint.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        var result = otpService.validateOtp(request.email(), request.otp());

        if (!result.valid()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message", result.message()));
        }

        if (request.purpose() == OtpPurpose.VERIFICATION) {
            userService.markUserVerified(request.email());
            return ResponseEntity.ok(Map.of("message", "Verified successfully, Continue to login"));
        }

        String token = jwtService.generateOtpToken(request.email());
        Map<String, Object> map = Map.of(
            "message", "OTP verified Successfully",
            "token", token,
            "time-stamp", Instant.now()
        );
        return ResponseEntity.ok(map);
    }

    /**
     * Sends an OTP to the provided email.
     * Can be used for PASSWORD_RESET, VERIFICATION, or any other supported purpose.
     * Frontend should handle redirection to /verify-otp after receiving this.
     */
    @PostMapping("/request-otp")
    public ResponseEntity<OtpResponse> requestOtp(@Valid @RequestBody EmailOnlyRequest request) {
        return ResponseEntity.ok(otpService.sendOtp(
            new OtpRequest(request.email(), OtpPurpose.PASSWORD_RESET)));
    }

    /**
     * Resets the user's password after successful OTP verification.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody ResetPasswordRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7);

            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid or expired.");
            }

            Boolean isOtpVerified = jwtService.extractClaim(token, claims -> claims.get("otp_verified", Boolean.class));
            String tokenType = jwtService.extractClaim(token, claims -> claims.get("token_type", String.class));

            if (isOtpVerified == null || !isOtpVerified) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("OTP not verified.");
            }

            if (!"otp".equals(tokenType)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token type.");
            }

            userService.resetPassword(request);
            return ResponseEntity.ok("Password reset successful. Please login with your new password.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }        
    }
}
