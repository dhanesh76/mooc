package com.dhanesh.auth.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record StudentProfileRequest(
    @NotBlank String fullName,
    String phoneNumber,
    @NotBlank String educationLevel,
    @NotBlank String preferredPlatform,
    @NotEmpty List<String> primaryInterests,
    String learningGoals,
    @NotBlank String preferredDifficultyLevel,
    List<String> hobbies
) {}
