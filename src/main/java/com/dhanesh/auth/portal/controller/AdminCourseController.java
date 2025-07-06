package com.dhanesh.auth.portal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.service.CourseService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing course operations by admin users.
 * Includes endpoints for adding, updating, and deleting courses.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseService courseService;

    /**
     * Adds a new course to the platform.
     *
     * @param course the course details
     * @return the added course with 201 Created status
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> addCourse(@RequestBody Course course){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.addCourse(course));        
    }

    /**
     * Adds multiple courses at once.
     *
     * @param courses list of courses to add
     * @return the list of added courses
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Course>> addCourses(@RequestBody List<Course> courses) {
        return ResponseEntity.ok(courseService.addCourses(courses));
    }

    /**
     * Updates an existing course by its ID.
     *
     * @param id the course ID to update
     * @param updatedData the updated course fields
     * @return the updated course, or 404 if not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @RequestBody Course updatedData) {
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
