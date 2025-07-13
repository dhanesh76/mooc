package com.dhanesh.auth.portal.controller;

import java.util.List;

import com.dhanesh.auth.portal.entity.SavedCourse;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.SavedCourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling operations related to saved courses by users.
 */
@Tag(name = "Saved Courses", description = "Endpoints for saving, retrieving, checking, and removing saved courses")
@RestController
@RequestMapping("/saved-courses")
@RequiredArgsConstructor
public class SavedCourseController {

    private final SavedCourseService savedCourseService;

    /**
     * Saves a course to the authenticated user's saved list.
     */
    @Operation(summary = "Save a course", description = "Saves a course to the logged-in user's saved list.")
    @PostMapping
    public ResponseEntity<SavedCourse> saveCourse(
            @AuthenticationPrincipal
            @Parameter(description = "Authenticated user") UserPrincipal principal,
            @RequestParam
            @Parameter(description = "ID of the course to save") String courseId) {
        return ResponseEntity.ok(savedCourseService.saveCourse(principal.getUser().getId(), courseId));
    }

    /**
     * Returns all saved courses of the authenticated user.
     */
    @Operation(summary = "Get all saved courses", description = "Retrieves all saved courses for the logged-in user.")
    @GetMapping
    public ResponseEntity<List<SavedCourse>> getSavedCourses(
            @AuthenticationPrincipal
            @Parameter(description = "Authenticated user") UserPrincipal principal) {
        return ResponseEntity.ok(savedCourseService.getSavedCourses(principal.getUser().getId()));
    }

    /**
     * Checks if a course is already saved by the user.
     */
    @Operation(summary = "Check if course is saved", description = "Returns true if the course is saved by the user.")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> isCourseSaved(
            @AuthenticationPrincipal
            @Parameter(description = "Authenticated user") UserPrincipal principal,
            @RequestParam
            @Parameter(description = "Course ID to check") String courseId) {
        boolean exists = savedCourseService.isCourseSaved(principal.getUser().getId(), courseId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Removes a course from the user's saved list.
     */
    @Operation(summary = "Remove a saved course", description = "Removes a course from the user's saved list.")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> removeSavedCourse(
            @AuthenticationPrincipal
            @Parameter(description = "Authenticated user") UserPrincipal principal,
            @PathVariable
            @Parameter(description = "ID of the course to remove") String courseId) {
        savedCourseService.removeSavedCourse(principal.getUser().getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the count of saved courses.
     */
    @Operation(summary = "Count saved courses", description = "Returns the total number of saved courses for the user.")
    @GetMapping("/count")
    public ResponseEntity<Long> countSavedCourses(
            @AuthenticationPrincipal
            @Parameter(description = "Authenticated user") UserPrincipal principal) {
        return ResponseEntity.ok(savedCourseService.getSavedCourseCount(principal.getUser().getId()));
    }
}
