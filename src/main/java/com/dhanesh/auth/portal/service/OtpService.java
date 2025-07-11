package com.dhanesh.auth.portal.service;

import java.security.SecureRandom;
import java.time.Instant;
import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.otp.OtpRequest;
import com.dhanesh.auth.portal.dto.otp.OtpResponse;
import com.dhanesh.auth.portal.dto.otp.OtpVerifyRequest;
import com.dhanesh.auth.portal.model.OtpData;
import com.dhanesh.auth.portal.model.OtpPurpose;
import com.dhanesh.auth.portal.model.OtpValidationResult;
import com.dhanesh.auth.portal.service.Redis.RedisOtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {
    
    private final EmailService emailService;
    private final RedisOtpService redisOtpService;

    /**
     * Generates a 6-digit numeric OTP, stores it against the user's email
     * with an expiration time, and returns the OTP.
     */
    public String generateOtp(String email, OtpPurpose purpose) {
        
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));

        redisOtpService.saveOtpData("otp:" + email, new OtpData(otp, purpose));
        return otp;
    }

    /**
     * Validates the OTP provided by the user.
     * Checks for existence, expiry, and correctness.
     * OTP is consumed after successful validation.
     */
    public OtpValidationResult validateOtp(OtpVerifyRequest request) {
        
        String key = "otp:"+request.email();
        
        //Otp is either expired or not at all requested 
        //must have been the case of expired because frontend would have 
        //redirected to this only if they have requested for otp earlier 
        if(!redisOtpService.hasKey(key))
            return new OtpValidationResult(false, "otp is expired, request for the new one ");

        OtpData generatedOtp = redisOtpService.getOtpData(key);

        /*
         * purpose of the otp mismatches 
         * not all possible cause user would have been able to request to 
         * reset password only if he already had registered
         */
        if(generatedOtp.purpose() != request.purpose())
            return new OtpValidationResult(false, "otp purpose mismatch");

        //invalid otp
        if(!generatedOtp.otp().equals(request.otp()))
            return new OtpValidationResult(false, "wrong otp");

        /*
         * if success fully verified remove the entry 
         */
        redisOtpService.deleteOtpData(key);
    
        return new OtpValidationResult(true, "verified successfully");
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

        String otp = generateOtp(email, purpose); // Generate and store OTP

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
