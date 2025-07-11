package com.dhanesh.auth.portal.dto.auth;

public record SignupTempData(
    String username, 
    String email, 
    String encodedPassword
    ) {}
