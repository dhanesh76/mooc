package com.dhanesh.auth.portal.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhanesh.auth.portal.dto.auth.ResetPasswordRequest;
import com.dhanesh.auth.portal.dto.auth.SigninRequest;
import com.dhanesh.auth.portal.dto.auth.SigninResponse;
import com.dhanesh.auth.portal.dto.auth.SignupRequest;
import com.dhanesh.auth.portal.dto.auth.SignupResponse;
import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.dto.otp.OtpResponse;
import com.dhanesh.auth.portal.dto.otp.OtpVerifyRequest;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.model.OtpValidationResult;
import com.dhanesh.auth.portal.security.jwt.JwtService;
import com.dhanesh.auth.portal.service.AuthService;
import com.dhanesh.auth.portal.service.OtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;
    private final OtpService otpService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        SigninResponse response = userService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        OtpValidationResult otpValidation = otpService.validateOtp(request);

        if (!otpValidation.valid()) {
            return ResponseEntity.badRequest().body(Map.of(
                "verified", false,
                "message", otpValidation.message()
            ));
        }

        if (request.purpose() == OtpPurpose.VERIFICATION) {
            if (userService.isRegisterSessionExpired(request.email())) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                    Map.of("message", "Registration session expired. Please register again.")
                );
            }

            userService.saveNewUser(request.email());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "User registered successfully. Please login.")
            );
        }

        // OTP for password reset
        String token = jwtService.generateOtpToken(request.email());
        return ResponseEntity.ok(Map.of(
            "message", "OTP verified successfully.",
            "token", token,
            "time-stamp", Instant.now()
        ));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<OtpResponse> requestOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(otpService.sendOtp(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody ResetPasswordRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        Map<String, Object> tokenValidation = userService.validatePasswordResetToken(request, authHeader);

        if (Boolean.FALSE.equals(tokenValidation.get("valid"))) {
            return ResponseEntity.badRequest().body((String) tokenValidation.get("message"));
        }

        userService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful. Please login with your new password.");
    }
}
