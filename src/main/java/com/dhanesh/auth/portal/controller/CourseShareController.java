package com.dhanesh.auth.portal.controller;

import java.util.Map;

import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.CourseShareService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling course sharing functionality.
 * Tracks share counts and returns the shareable link.
 */
@Tag(name = "Course Sharing", description = "Endpoints for sharing courses and tracking share analytics")
@RestController
@RequestMapping("/share-course")
@RequiredArgsConstructor
public class CourseShareController {

    private final CourseShareService courseShareService;

    /**
     * Generates a shareable URL for a course and tracks the sharing event.
     *
     * @param courseId the ID of the course to be shared
     * @param platform (optional) platform where the course was shared (e.g. WhatsApp, Telegram)
     * @param principal the authenticated user (nullable for public sharing)
     * @return map containing message and shareable URL
     */
    @Operation(
        summary = "Share a course",
        description = "Tracks a course share event and returns a shareable course URL"
    )
    @PostMapping
    public ResponseEntity<Map<String, String>> shareCourse(
        @RequestParam
        @Parameter(description = "ID of the course to share") String courseId,

        @RequestParam(required = false)
        @Parameter(description = "Platform where the course is shared (optional)") String platform,

        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user, optional for anonymous sharing") UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getUser().getId() : null;

        // Track the share event
        courseShareService.recordShare(courseId, userId, platform);

        // Construct the public share URL (adjust this based on prod base URL)
        String shareableUrl = "http://localhost:3000/courses/" + courseId;

        return ResponseEntity.ok(Map.of(
            "message", "Course shared successfully",
            "shareableUrl", shareableUrl
        ));
    }
}
