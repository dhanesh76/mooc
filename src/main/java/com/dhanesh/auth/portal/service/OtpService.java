package com.dhanesh.auth.portal.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.dto.otp.OtpValidationResult;
import com.dhanesh.auth.portal.dto.otp.OtpVerifyRequest;
import com.dhanesh.auth.portal.model.OtpData;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.service.Redis.RedisOtpService;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing OTP (One-Time Password) generation, validation, and delivery via email.
 * Supports multiple OTP purposes such as registration verification and password reset.
 */
@Service
@RequiredArgsConstructor
public class OtpService {
    
    private final EmailService emailService;
    private final RedisOtpService redisOtpService;

    @Value("${app.otp.duration}")
    private long otpDuration;

    /**
     * Generates a 6-digit numeric OTP, stores it in Redis against the user's email with a purpose,
     * and returns the generated OTP.
     *
     * @param email   the recipient's email address
     * @param purpose the purpose of the OTP (e.g., VERIFICATION or PASSWORD_RESET)
     * @return the generated OTP string
     */
    public String generateOtp(String email, OtpPurpose purpose) {
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        redisOtpService.saveOtpData("otp:" + email, new OtpData(otp, purpose));
        return otp;
    }

    /**
     * Validates the provided OTP against the stored OTP in Redis.
     * Also checks for purpose match and expiration. OTP is deleted upon successful validation.
     *
     * @param request the OTP verification request containing email, OTP, and purpose
     * @return validation result with status and message
     */
    public OtpValidationResult validateOtp(OtpVerifyRequest request) {
        String key = "otp:" + request.email();

        if (!redisOtpService.hasKey(key)) {
            return new OtpValidationResult(false, "otp is expired, request for the new one");
        }

        OtpData generatedOtp = redisOtpService.getOtpData(key);

        if (generatedOtp.purpose() != request.purpose()) {
            return new OtpValidationResult(false, "otp purpose mismatch");
        }

        if (!generatedOtp.otp().equals(request.otp())) {
            return new OtpValidationResult(false, "wrong otp");
        }

        redisOtpService.deleteOtpData(key);
        return new OtpValidationResult(true, "verified successfully");
    }

    /**
     * Sends a generated OTP to the user's email based on the specified purpose.
     * The subject and message body are dynamically built based on the OTP purpose.
     *
     * @param otpRequest the OTP request containing email and purpose
     * @return true if the OTP was sent successfully
     */
    public boolean sendOtp(OtpRequest otpRequest) {
        String email = otpRequest.email();
        OtpPurpose purpose = otpRequest.purpose();

        String otp = generateOtp(email, purpose);

        String subject;
        String body;

        switch (purpose) {
            case PASSWORD_RESET -> {
                subject = "OTP for Password Reset";
                body = "Dear user,\n\nUse the following OTP to reset your password: " + otp +
                        "\nThis OTP is valid for " + otpDuration + " minutes.\n\nRegards,\nMOOC";
            }
            case VERIFICATION -> {
                subject = "OTP for Email Verification";
                body = "Dear user,\n\nUse the following OTP to verify your email: " + otp +
                        "\nThis OTP is valid for " + otpDuration + " minutes.\n\nRegards,\nMOOC";
            }
            default -> throw new IllegalArgumentException("Invalid OTP purpose.");
        }

        emailService.sendOtp(email, subject, body);
        return true;
    }
}
