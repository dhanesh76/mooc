package com.dhanesh.auth.portal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    private String id; // maps to userId from users table

    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;

    private String educationLevel;           // e.g., DIPLOMA, UG, PG, SCHOOL
    private String preferredPlatform;
    private String preferredDifficultyLevel; // e.g., BEGINNER, INTERMEDIATE, ADVANCED

    private List<String> primaryInterests;
    private List<String> hobbies;
    private String learningGoals;

    private boolean profileCompleted;
}
