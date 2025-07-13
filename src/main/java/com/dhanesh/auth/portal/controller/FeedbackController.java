package com.dhanesh.auth.portal.controller;

import java.util.List;

import com.dhanesh.auth.portal.dto.feedback.FeedbackSubmissionRequest;
import com.dhanesh.auth.portal.entity.CourseFeedback;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.CourseFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for submitting and viewing course feedback.
 */
@Tag(name = "Course Feedback", description = "Submit and retrieve feedback for courses")
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final CourseFeedbackService feedbackService;

    /**
     * Submits feedback for a set of recommended courses.
     * Only one feedback submission is allowed per recommendation session.
     *
     * @param principal the authenticated user
     * @param request the feedback submission payload
     * @return success message if submitted
     */
    @Operation(
        summary = "Submit feedback for recommended courses",
        description = "Accepts a list of feedbacks for recommended courses. Only allowed once per recommendation set."
    )
    @PostMapping
    public ResponseEntity<String> submitCourseFeedback(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal,

        @Valid @RequestBody
        @Parameter(description = "Feedback submission payload") FeedbackSubmissionRequest request
    ) {
        feedbackService.submitFeedbacks(principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Feedback submitted successfully");
    }

    /**
     * Retrieves all feedback submitted by the authenticated user.
     *
     * @param principal the authenticated user
     * @return list of feedback entries submitted by the user
     */
    @Operation(
        summary = "Get all feedbacks by current user",
        description = "Returns all feedback entries submitted by the authenticated user"
    )
    @GetMapping("/all")
    public ResponseEntity<List<CourseFeedback>> allFeedbackByUserId(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal
    ) {
        return ResponseEntity.ok(feedbackService.allFeedbacksByUserId(principal.getUser().getId()));
    }

    /**
     * Retrieves all feedback submitted for a specific course.
     *
     * @param courseId the course ID
     * @return list of feedback entries for the course
     */
    @Operation(
        summary = "Get all feedbacks for a course",
        description = "Returns all feedback entries submitted for the given course ID"
    )
    @GetMapping("/all/{courseId}")
    public ResponseEntity<List<CourseFeedback>> allFeedbackByCourseId(
        @PathVariable
        @Parameter(description = "Course ID to fetch feedback for") String courseId
    ) {
        return ResponseEntity.ok(feedbackService.allFeedbackByCourseId(courseId));
    }
}
