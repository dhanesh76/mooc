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
    private String userId; // same as Users table ID

    private String fullName;
    private String email;
    private String phoneNumber;

    private String educationLevel; // Enum recommended: DIPLOMA, UG, PG, SCHOOL
    private String preferredPlatform;

    private List<String> primaryInterests;
    private String learningGoals;

    private String preferredDifficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private List<String> hobbies;

    private boolean profileCompleted;
}
