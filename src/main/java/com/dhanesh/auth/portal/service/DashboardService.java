package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.DashboardResponse;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentProfileService profileService;
    private final SavedCourseService savedCourseService;
    private final CourseRepository courseRepository;

    /**
     * Builds the dashboard view: student profile, saved courses, and top recommended ones.
     */
    public DashboardResponse getDashboard(String userId) {
        StudentProfile profile = profileService.getProfile(userId);

        // Fetch saved course IDs and map to Course entities
        List<String> savedCourseIds = savedCourseService.getSavedCourses(userId)
                                            .stream()
                                            .map(saved -> saved.getCourseId())
                                            .collect(Collectors.toList());

        List<Course> savedCourses = courseRepository.findAllById(savedCourseIds);

        // Fetch all courses and rank based on user preferences
        List<Course> allCourses = courseRepository.findAll();

        List<Course> recommendedCourses = allCourses.stream()
            .map(course -> {
                int score = 0;

                if (course.getTags() != null &&
                    !profile.getPrimaryInterests().isEmpty() &&
                    profile.getPrimaryInterests().stream().anyMatch(course.getTags()::contains)) {
                    score += 50;
                }

                if (profile.getPreferredPlatform() != null &&
                    profile.getPreferredPlatform().equalsIgnoreCase(course.getPlatform())) {
                    score += 30;
                }

                if (profile.getPreferredDifficultyLevel() != null &&
                    profile.getPreferredDifficultyLevel().equalsIgnoreCase(course.getDifficultyLevel())) {
                    score += 20;
                }

                return new AbstractMap.SimpleEntry<>(course, score);
            })
            .filter(entry -> entry.getValue() > 0)
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return new DashboardResponse(
            profile,
            savedCourses,
            recommendedCourses
        );
    }
}
