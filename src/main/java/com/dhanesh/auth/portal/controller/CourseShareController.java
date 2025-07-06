package com.dhanesh.auth.portal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.CourseShareService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/share-course")
@RequiredArgsConstructor
public class CourseShareController {

    private final CourseShareService courseShareService;

    @PostMapping
    public ResponseEntity<Map<String, String>> shareCourse(
        @RequestParam String courseId,
        @RequestParam(required = false) String platform,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getUser().getId() : null;

        // Track the share
        courseShareService.recordShare(courseId, userId, platform);

        // Construct the shareable link
        String shareableUrl = "http://localhost:3000/courses/" + courseId;

        // Return the link in the response
        Map<String, String> response = Map.of(
            "message", "Course shared successfully",
            "shareableUrl", shareableUrl
        );

        return ResponseEntity.ok(response);
    }
}
