package com.dhanesh.auth.portal.controller;

import org.springframework.http.ResponseEntity;

import com.dhanesh.auth.portal.entity.SavedCourse;
import com.dhanesh.auth.portal.service.SavedCourseService;

import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved-courses")
@RequiredArgsConstructor
public class SavedCourseController {

    private final SavedCourseService savedCourseService;

    @PostMapping
    public ResponseEntity<SavedCourse> saveCourse(
            @RequestParam String userId,
            @RequestParam String courseId
    ) {
        
        return ResponseEntity.ok(savedCourseService.saveCourse(userId, courseId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SavedCourse>> getSavedCourses(@PathVariable String userId) {
        return ResponseEntity.ok(savedCourseService.getSavedCourses(userId));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> isCourseSaved(
            @RequestParam String userId,
            @RequestParam String courseId
    ) {
        boolean exists = savedCourseService.isCourseSaved(userId, courseId);
        return ResponseEntity.ok(exists);
    }

    //  Remove saved course
    @DeleteMapping("/{userId}/{courseId}")
    public ResponseEntity<Void> removeSavedCourse(
            @PathVariable String userId,
            @PathVariable String courseId
    ) {
        savedCourseService.removeSavedCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Long> countSavedCourses(@PathVariable String userId) {
        long count = savedCourseService.getSavedCourseCount(userId);
        return ResponseEntity.ok(count);
    }

}
