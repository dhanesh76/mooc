package com.dhanesh.auth.portal.dto.feedback;

import java.util.List;

public record FeedbackSubmissionRequest(
    List<CourseFeedbackRequest> courseFeedbacks
) {}
