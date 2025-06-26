package com.dhanesh.auth.portal.dto.otp;

import com.dhanesh.auth.portal.model.OtpPurpose;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpRequest(
    @NotBlank(message = "email can't be empty")
    @Email(message = "Invalid email")
    String email,

    @NotNull
    OtpPurpose purpose
){}
