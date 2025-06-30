package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.DashboardResponse;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentProfileService profileService;
    private final SavedCourseService savedCourseService;
    private final CourseRepository courseRepository;

    public DashboardResponse getDashboard(String userId) {
        StudentProfile profile = profileService.getProfile(userId);

        // Fetch saved course IDs and convert to Course entities
        List<String> savedCourseIds = savedCourseService.getSavedCourses(userId)
                                            .stream()
                                            .map(saved -> saved.getCourseId())
                                            .collect(Collectors.toList());

        List<Course> savedCourses = courseRepository.findAllById(savedCourseIds);

        // Fetch recommended courses based on user interests, platform, difficulty
        List<Course> allCourses = courseRepository.findAll();

        List<Course> recommendedCourses = allCourses.stream()
            .map(course -> {
                int score = 0;

                // Tag match: if any user interest matches a course tag
                if (course.getTags() != null && 
                    !profile.getPrimaryInterests().isEmpty() &&
                    profile.getPrimaryInterests().stream().anyMatch(course.getTags()::contains)) {
                    score += 50;
                }

                // Platform match
                if (profile.getPreferredPlatform() != null &&
                    profile.getPreferredPlatform().equalsIgnoreCase(course.getPlatform())) {
                    score += 30;
                }

                // Difficulty match
                if (profile.getPreferredDifficultyLevel() != null &&
                    profile.getPreferredDifficultyLevel().equalsIgnoreCase(course.getDifficultyLevel())) {
                    score += 20;
                }

                // Wrap course and score into a pair (Course + score)
                return new AbstractMap.SimpleEntry<>(course, score);
            })
            .filter(entry -> entry.getValue() > 0) // Keep only courses with some match
            .sorted((a, b) -> b.getValue() - a.getValue()) // Sort by score descending
            .limit(5) // Return top 10
            .map(entry -> entry.getKey()) // Extract course
            .collect(Collectors.toList());

        return new DashboardResponse(
            profile,
            savedCourses,
            recommendedCourses
        );
    }
}
