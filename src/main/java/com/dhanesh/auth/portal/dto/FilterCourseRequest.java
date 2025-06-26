package com.dhanesh.auth.portal.dto;

import java.util.List;

import com.dhanesh.auth.portal.model.DifficultyLevel;

public record FilterCourseRequest(
        List<String> tags, 
        List<String> platforms, 
        DifficultyLevel difficulty, 
        String duration
    ) {
}
