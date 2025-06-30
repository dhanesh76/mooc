package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.service.CourseService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/courses")
@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(courseService.getAllCourses(page, size));
    }
    
    /**
     * Search Bar 
     * @param q
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String q) {
        return ResponseEntity.ok(courseService.searchCourses(q));
    }

    /**
     * 
     * @param request
     * @return courses based on user interest(platform({}), domain({tags}), difficulty)
     */
    @PostMapping("/filter")
    public ResponseEntity<List<Course>> filterCourses(@AuthenticationPrincipal Users user, @Valid @RequestBody FilterCourseRequest request) {
        return ResponseEntity.ok(courseService.filterCourses(request, user.getId()));
    }

    @GetMapping("/share/{courseId}")
    public ResponseEntity<?> handleShareRedirect(@PathVariable String courseId, HttpServletResponse response) {
        courseService.incrementShareCount(courseId);
        String redirectUrl = "https://your-frontend.com/shared-course/" + courseId;

        response.setHeader("Location", redirectUrl);
        return ResponseEntity.status(302).build(); // temporary redirect
    }

}