package com.dhanesh.auth.portal.dto.otp;

import com.dhanesh.auth.portal.model.OtpPurpose;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// For verifying OTP
public record OtpVerifyRequest(
    @NotBlank @Email String email,
    @NotBlank String otp,
    @NotNull OtpPurpose purpose
) {}