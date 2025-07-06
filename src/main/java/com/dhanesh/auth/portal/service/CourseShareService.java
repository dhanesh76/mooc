package com.dhanesh.auth.portal.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.entity.CourseShare;
import com.dhanesh.auth.portal.repository.CourseRepository;
import com.dhanesh.auth.portal.repository.CourseShareRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseShareService {

    private final CourseShareRepository courseShareRepository;
    private final CourseRepository courseRepository;

    public void recordShare(String courseId, String userId, String platform) {
        // Validate course existence
        var courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Course with ID '" + courseId + "' not found.");
        }

        // Increment share count
        var course = courseOpt.get();
        course.setShareCount(course.getShareCount() + 1);
        courseRepository.save(course);

        // Normalize platform value
        String finalPlatform = (platform != null && !platform.isBlank()) ? platform : "unknown";

        // Save share details if user is logged in
        if (userId != null && !userId.isBlank()) {
            CourseShare courseShare = CourseShare.builder()
                    .courseId(courseId)
                    .userId(userId)
                    .platform(finalPlatform)
                    .sharedAt(Instant.now())
                    .build();
            courseShareRepository.save(courseShare);
        }
    }
}
