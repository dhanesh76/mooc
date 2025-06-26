package com.dhanesh.auth.portal.dto;

import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.StudentProfile;
import java.util.List;

public record DashboardResponse(
    StudentProfile profile,
    List<Course> savedCourses,
    List<Course> recommendedCourses
) {}
