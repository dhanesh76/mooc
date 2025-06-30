package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dhanesh.auth.portal.entity.SavedCourse;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.service.SavedCourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/saved-courses")
@RequiredArgsConstructor
public class SavedCourseController {

    private final SavedCourseService savedCourseService;

    @PostMapping
    public ResponseEntity<SavedCourse> saveCourse(
            @AuthenticationPrincipal Users user,
            @RequestParam String courseId) {
        return ResponseEntity.ok(savedCourseService.saveCourse(user.getId(), courseId));
    }

    @GetMapping
    public ResponseEntity<List<SavedCourse>> getSavedCourses(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(savedCourseService.getSavedCourses(user.getId()));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> isCourseSaved(
            @AuthenticationPrincipal Users user,
            @RequestParam String courseId) {
        boolean exists = savedCourseService.isCourseSaved(user.getId(), courseId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> removeSavedCourse(
            @AuthenticationPrincipal Users user,
            @PathVariable String courseId) {
        savedCourseService.removeSavedCourse(user.getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countSavedCourses(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(savedCourseService.getSavedCourseCount(user.getId()));
    }
}
