package com.dhanesh.auth.portal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record FeedbackRequest(
    @NotBlank String userId,
    @Min(1) @Max(5) int recommendationRating,
    @NotBlank String relevance,
    boolean enrolled,
    @Min(1) @Max(5) Integer courseQualityRating, // nullable
    String feedback,
    @NotBlank String appExperience
) {}
