package com.dhanesh.auth.portal.dto.auth;

import com.dhanesh.auth.portal.model.AuthProvider;

public record SignupResponse (
    String username,
    String email,
    String message,
    AuthProvider authProvider
){}
