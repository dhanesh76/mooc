package com.dhanesh.auth.portal.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record StudentProfileRequest(
    @NotBlank String fullName,
    @NotBlank String email,
    String phoneNumber,
    @NotBlank String educationLevel,
    @NotBlank String preferredPlatform,
    @NotBlank List<String> primaryInterests,
    String learningGoals,
    @NotBlank String preferredDifficultyLevel,
    List<String> hobbies
) {}
