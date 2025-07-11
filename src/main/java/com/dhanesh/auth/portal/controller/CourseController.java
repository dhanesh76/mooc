package com.dhanesh.auth.portal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.CourseService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for public course-related actions including retrieval, filtering,
 * searching, and share-link handling.
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Returns paginated list of all available courses.
     *
     * @param page the page number (default = 0)
     * @param size the number of courses per page (default = 10)
     * @return list of courses
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(courseService.getAllCourses(page, size));
    }

    /**
     * Performs search on courses by query string (used in search bar).
     *
     * @param q the search term
     * @return list of matching courses
     */
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String q) {
        return ResponseEntity.ok(courseService.searchCourses(q));
    }

    /**
     * Filters courses based on user's preferences (domain, difficulty, platform).
     *
     * @param principal the authenticated user
     * @param request the filter options
     * @return filtered course list
     */
    @PostMapping("/filter")
    public ResponseEntity<List<Course>> filterCourses(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FilterCourseRequest request
    ) {
        return ResponseEntity.ok(courseService.filterCourses(request, principal.getUser().getId()));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getCourse(@PathVariable String courseId) {
        Optional<Course> course = courseService.getCourseById(courseId);
        
       return course.isPresent() ? ResponseEntity.ok().body(course) 
        : ResponseEntity.badRequest().body("Invalid Course ID");
    }
}
