package com.dhanesh.auth.portal.dto;

import java.util.List;
import java.util.Map;

import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.CourseFeedback;
import com.dhanesh.auth.portal.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {

    private String adminName;
    private String adminEmail;
    private String role;

    private long totalCourses;
    private List<Course> recentCourses;

    private long totalUsers;
    private List<Users> recentUsers;

    private long feedbackCount;
    private List<CourseFeedback> recentFeedback;

    private Map<String, Long> platformDistribution;
    private Map<String, Long> interestAreaPopularity;
}
