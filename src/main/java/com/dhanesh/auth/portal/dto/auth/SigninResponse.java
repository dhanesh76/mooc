package com.dhanesh.auth.portal.dto.auth;

import java.util.Date;

import com.dhanesh.auth.portal.model.AuthProvider;

public record SigninResponse(
    String id,
    String loginId,
    String role,
    String token,
    Date expiresAt,
    AuthProvider authProvider
) {}
