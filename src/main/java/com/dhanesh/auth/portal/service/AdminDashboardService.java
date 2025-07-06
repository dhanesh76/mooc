package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.AdminDashboardResponse;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.CourseFeedback;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.repository.CourseFeedbackRepository;
import com.dhanesh.auth.portal.repository.CourseRepository;
import com.dhanesh.auth.portal.repository.StudentProfileRepository;
import com.dhanesh.auth.portal.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates an overview of the system's state for the authenticated admin.
 * 
 * Includes:
 * - Admin identity info
 * - Total & recent courses
 * - Total & recent users
 * - Feedback count and recent feedbacks
 * - Platform distribution statistics
 * - Interest area popularity among students
 *
 * @param usernameOrEmail username or email extracted from the logged-in principal
 * @return populated AdminDashboardResponse DTO
 */

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final CourseRepository courseRepository;
    private final CourseFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    public AdminDashboardResponse getDashboard(String usernameOrEmail) {
        Users admin = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // Fetch course stats
        long totalCourses = courseRepository.count();
        List<Course> recentCourses = courseRepository
            .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "lastUpdated")))
            .getContent();

        // Fetch user stats
        long totalUsers = userRepository.count();
        List<Users> recentUsers = userRepository
            .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
            .getContent();

        // Feedback
        long feedbackCount = feedbackRepository.count();
        List<CourseFeedback> recentFeedback = feedbackRepository
            .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
            .getContent();

        // Platform Distribution
        Map<String, Long> platformDist = courseRepository
            .findAll()
            .stream()
            .filter(c -> c.getPlatform() != null)
            .collect(Collectors.groupingBy(Course::getPlatform, Collectors.counting()));

        // Interest Area Popularity
        Map<String, Long> interestStats = studentProfileRepository
            .findAll()
            .stream()
            .filter(p -> p.getPrimaryInterests() != null)
            .flatMap(p -> p.getPrimaryInterests().stream())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return AdminDashboardResponse.builder()
            .adminName(admin.getUsername())
            .adminEmail(admin.getEmail())
            .role(admin.getRole())

            .totalCourses(totalCourses)
            .recentCourses(recentCourses)

            .totalUsers(totalUsers)
            .recentUsers(recentUsers)

            .feedbackCount(feedbackCount)
            .recentFeedback(recentFeedback)

            .platformDistribution(platformDist)
            .interestAreaPopularity(interestStats)
            .build();
    }
}
