package com.dhanesh.auth.portal.dto.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CourseFeedbackRequest(
    String courseId,
    @Min(1) @Max(5) int rating,
    boolean enrolled,
    String comments
) {}
