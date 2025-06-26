package com.dhanesh.auth.portal.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.dto.otp.OtpResponse;
import com.dhanesh.auth.portal.model.OtpData;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.model.OtpValidationResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {
    
    private final EmailService emailService;
    
    private static final long EXPIRY_DURATION_SECONDS = 300; // OTP valid for 5 minutes
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();


    /**
     * Generates a 6-digit numeric OTP, stores it against the user's email
     * with an expiration time, and returns the OTP.
     */
    public String generateOtp(String email) {
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));

        // Store OTP with expiration timestamp
        OtpData data = new OtpData(otp, Instant.now().plusSeconds(EXPIRY_DURATION_SECONDS));

        otpStorage.put(email, data);
        return otp;
    }

    /**
     * Validates the OTP provided by the user.
     * Checks for existence, expiry, and correctness.
     * OTP is consumed after successful validation.
     */
    public OtpValidationResult validateOtp(String email, String providedOtp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            return new OtpValidationResult(false, "No OTP was requested for this email.");
        }

        if (Instant.now().isAfter(otpData.expiry())) {
            otpStorage.remove(email); // Auto-clean expired OTP
            return new OtpValidationResult(false, "OTP has expired. Please request a new one.");
        }

        if (!otpData.otp().equals(providedOtp)) {
            return new OtpValidationResult(false, "Invalid OTP. Please try again.");
        }

        otpStorage.remove(email); // OTP is consumed after success
        return new OtpValidationResult(true, "OTP validated successfully.");
    }

    /**
     * Forcefully clears OTP from storage (used optionally).
     */
    public void clearOtp(String email) {
        otpStorage.remove(email);
    }

    /**
     * Handles sending OTP based on purpose:
     * - For PASSWORD_RESET or VERIFICATION
     * - Formats the subject and body accordingly
     * - Delegates email sending to EmailService
     */
    public OtpResponse sendOtp(OtpRequest otpRequest) {
        String email = otpRequest.email();
        OtpPurpose purpose = otpRequest.purpose();

        String otp = generateOtp(email); // Generate and store OTP

        String subject;
        String body;

        // Choose email content based on purpose
        switch (purpose) {
            case PASSWORD_RESET -> {
                subject = "OTP for Password Reset";
                body = "Dear user,\n\nUse the following OTP to reset your password: " + otp +
                        "\nThis OTP is valid for 5 minutes.\n\nRegards,\nAuth Portal";
            }
            case VERIFICATION -> {
                subject = "OTP for Email Verification";
                body = "Dear user,\n\nUse the following OTP to verify your email: " + otp +
                        "\nThis OTP is valid for 5 minutes.\n\nRegards,\nAuth Portal";
            }
            default -> throw new IllegalArgumentException("Invalid OTP purpose.");
        }

        emailService.sendOtp(email, subject, body);

        return new OtpResponse("OTP sent successfully to your email", Instant.now());
    }
}
