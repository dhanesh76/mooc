package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhanesh.auth.portal.dto.feedback.FeedbackSubmissionRequest;
import com.dhanesh.auth.portal.entity.CourseFeedback;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.CourseFeedbackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final CourseFeedbackService feedbackService;

    /**
     * Accepts feedback for each of the 3 recommended courses.
     * One form submission per recommendation set.
     */
    @PostMapping
    public ResponseEntity<String> submitCourseFeedback(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody FeedbackSubmissionRequest request) {
        
            feedbackService.submitFeedbacks(principal.getUser().getId() ,request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Feedback submitted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseFeedback>> allFeedbackByUserId(@AuthenticationPrincipal UserPrincipal principal){
        return ResponseEntity
            .ok()
            .body(feedbackService.allFeedbacksByUserId(principal.getUser().getId()));
    }

    @GetMapping("/all/{courseId}")
    public ResponseEntity<List<CourseFeedback>> allFeedbackByCourseId(@PathVariable String courseId){
        return ResponseEntity.ok().body(feedbackService.allFeedbackByCourseId(courseId));
    }
}
