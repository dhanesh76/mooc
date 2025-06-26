package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.DashboardResponse;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        List<Course> recommendedCourses = courseRepository.findAll()
            .stream()
            .filter(course ->
                course.getTags() != null &&
                profile.getPrimaryInterests() != null &&
                profile.getPrimaryInterests().stream().anyMatch(course.getTags()::contains) &&
                profile.getPreferredPlatform().equalsIgnoreCase(course.getPlatform()) &&
                profile.getPreferredDifficultyLevel().equalsIgnoreCase(course.getDifficultyLevel())
            )

            .limit(5)
            .collect(Collectors.toList());

        return new DashboardResponse(profile, savedCourses, recommendedCourses);
    }
}
