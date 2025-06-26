package com.dhanesh.auth.portal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhanesh.auth.portal.dto.FeedbackRequest;
import com.dhanesh.auth.portal.entity.Feedback;
import com.dhanesh.auth.portal.service.FeedbackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackRequest request){
        Feedback response = feedbackService.submitFeedback(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
