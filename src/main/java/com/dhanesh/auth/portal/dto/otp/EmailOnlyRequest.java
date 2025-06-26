package com.dhanesh.auth.portal.dto.otp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailOnlyRequest(@NotBlank @Email String email) {}

