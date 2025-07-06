package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.entity.SavedCourse;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.SavedCourseService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for handling operations related to saved courses by users.
 */
@RestController
@RequestMapping("/saved-courses")
@RequiredArgsConstructor
public class SavedCourseController {

    private final SavedCourseService savedCourseService;

    /**
     * Saves a course to the user's saved list.
     *
     * @param principal the authenticated user
     * @param courseId  the course to save
     * @return the saved course object
     */
    @PostMapping
    public ResponseEntity<SavedCourse> saveCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String courseId) {
        return ResponseEntity.ok(savedCourseService.saveCourse(principal.getUser().getId(), courseId));
    }

    /**
     * Retrieves all saved courses for the authenticated user.
     *
     * @param principal the authenticated user
     * @return list of saved courses
     */
    @GetMapping
    public ResponseEntity<List<SavedCourse>> getSavedCourses(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(savedCourseService.getSavedCourses(principal.getUser().getId()));
    }

    /**
     * Checks if a specific course is saved by the authenticated user.
     *
     * @param principal the authenticated user
     * @param courseId  the course ID to check
     * @return true if saved, false otherwise
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> isCourseSaved(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String courseId) {
        boolean exists = savedCourseService.isCourseSaved(principal.getUser().getId(), courseId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Removes a course from the user's saved list.
     *
     * @param principal the authenticated user
     * @param courseId  the course ID to remove
     * @return 204 No Content on success
     */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> removeSavedCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String courseId) {
        savedCourseService.removeSavedCourse(principal.getUser().getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the count of saved courses for the authenticated user.
     *
     * @param principal the authenticated user
     * @return number of saved courses
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSavedCourses(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(savedCourseService.getSavedCourseCount(principal.getUser().getId()));
    }
}
