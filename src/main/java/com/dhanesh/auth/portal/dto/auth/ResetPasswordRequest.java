package com.dhanesh.auth.portal.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @Email
    @NotBlank
    String email,

    @NotBlank
    String newPassword
) {}
