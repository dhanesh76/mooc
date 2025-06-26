package com.dhanesh.auth.portal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/courses")
@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    
    /**
    * END POINT FOR - SEARCH BAR 
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
    public ResponseEntity<List<Course>> filterCourses(@Valid @RequestBody FilterCourseRequest request) {
        return ResponseEntity.ok(courseService.filterCourses(request));
    }
}