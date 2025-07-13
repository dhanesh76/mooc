package com.dhanesh.auth.portal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller for managing course operations by admin users.
 * Only accessible to users with ROLE_ADMIN.
 * Includes endpoints for adding, updating, and deleting courses.
 */
@Tag(name = "Admin Course Management", description = "Endpoints for admins to manage courses")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseService courseService;

    /**
     * Adds a new course to the platform.
     *
     * @param course the course details to be added
     * @return the added course with 201 Created status
     */
    @Operation(summary = "Add a new course", description = "Allows admin to add a single new course.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> addCourse(
        @RequestBody 
        @Parameter(description = "Course object to be added", required = true) Course course
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.addCourse(course));        
    }

    /**
     * Adds multiple courses at once.
     *
     * @param courses list of courses to add
     * @return the list of added courses
     */
    @Operation(summary = "Add multiple courses", description = "Allows admin to add a list of courses in bulk.")
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> addCourses(
        @RequestBody 
        @Parameter(description = "List of courses to be added", required = true) List<Course> courses
    ) {
        return ResponseEntity.ok(courseService.addCourses(courses));
    }

    /**
     * Updates an existing course by its ID.
     *
     * @param id the course ID to update
     * @param updatedData the updated course fields
     * @return the updated course, or 404 if not found
     */
    @Operation(summary = "Update a course", description = "Allows admin to update an existing course by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(
        @PathVariable 
        @Parameter(description = "ID of the course to update", required = true) String id,

        @RequestBody 
        @Parameter(description = "Updated course data", required = true) Course updatedData
    ) {
        Optional<Course> course = courseService.updateCourse(id, updatedData);
        return course.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id the course ID to delete
     * @return 204 No Content if successfully deleted
     */
    @Operation(summary = "Delete a course", description = "Allows admin to delete a course by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
        @PathVariable 
        @Parameter(description = "ID of the course to delete", required = true) String id
    ) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
