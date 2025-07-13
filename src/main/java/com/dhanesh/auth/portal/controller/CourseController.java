package com.dhanesh.auth.portal.controller;

import java.util.List;
import java.util.Optional;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.CourseService;

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
 * Controller for public course-related actions including:
 * - Pagination
 * - Search
 * - Filter
 * - Share link access
 */
@Tag(name = "Courses", description = "Endpoints for fetching, filtering, and searching courses")
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
    @Operation(summary = "Get all courses", description = "Returns paginated list of all available courses")
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") 
            @Parameter(description = "Page number (default = 0)") int page,

            @RequestParam(defaultValue = "10") 
            @Parameter(description = "Number of courses per page (default = 10)") int size
    ) {
        return ResponseEntity.ok(courseService.getAllCourses(page, size));
    }

    /**
     * Performs search on courses by query string (used in search bar).
     *
     * @param q the search term
     * @return list of matching courses
     */
    @Operation(summary = "Search courses", description = "Searches for courses by a keyword or phrase")
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(
        @RequestParam 
        @Parameter(description = "Search query (e.g. Java, Spring)") String q
    ) {
        return ResponseEntity.ok(courseService.searchCourses(q));
    }

    /**
     * Filters courses based on user's preferences (domain, difficulty, platform, etc.)
     *
     * @param principal the authenticated user
     * @param request the filter options
     * @return filtered course list
     */
    @Operation(summary = "Filter courses", description = "Filters courses based on preferences like domain, platform, difficulty, etc.")
    @PostMapping("/filter")
    public ResponseEntity<List<Course>> filterCourses(
            @AuthenticationPrincipal 
            @Parameter(description = "Authenticated user") UserPrincipal principal,

            @Valid @RequestBody 
            @Parameter(description = "Filtering preferences like domain, difficulty, etc.") FilterCourseRequest request
    ) {
        return ResponseEntity.ok(courseService.filterCourses(request, principal.getUser().getId()));
    }

    /**
     * Fetches a specific course by its ID.
     *
     * @param courseId the course ID
     * @return the course or error message
     */
    @Operation(summary = "Get course by ID", description = "Returns details of a specific course by its ID")
    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getCourse(
        @PathVariable 
        @Parameter(description = "Unique ID of the course") String courseId
    ) {
        Optional<Course> course = courseService.getCourseById(courseId);

        return course
            .<ResponseEntity<Object>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Course ID"));
    }
}
