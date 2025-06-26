package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.StudentProfileRequest;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository repository;

    public StudentProfile createOrUpdateProfile(String userId, StudentProfileRequest request) {
        StudentProfile profile = StudentProfile.builder()
            .userId(userId)
            .fullName(request.fullName())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .educationLevel(request.educationLevel())
            .preferredPlatform(request.preferredPlatform())
            .primaryInterests(request.primaryInterests())
            .learningGoals(request.learningGoals())
            .preferredDifficultyLevel(request.preferredDifficultyLevel())
            .hobbies(request.hobbies())
            .profileCompleted(true)
            .build();

        return repository.save(profile);
    }

    public boolean isProfileCompleted(String userId) {
        return repository.existsByUserId(userId);
    }

    public StudentProfile getProfile(String userId) {
        return repository.findById(userId).orElse(null);
    }
}
