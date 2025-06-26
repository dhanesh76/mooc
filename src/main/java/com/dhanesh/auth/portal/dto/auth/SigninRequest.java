package com.dhanesh.auth.portal.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SigninRequest (
    @NotBlank(message = "field can't be empty") String loginId,  
    @NotBlank(message = "enter a valid pssword") String password
){}

