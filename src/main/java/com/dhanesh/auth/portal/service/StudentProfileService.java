package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.StudentProfileRequest;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.repository.StudentProfileRepository;
import com.dhanesh.auth.portal.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for managing student profile logic.
 */
@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository repository;
    private final UserRepository userRepository;

    /**
     * Creates a new student profile if one doesn't already exist.
     *
     * @param id the user ID
     * @param request the profile creation request data
     * @return the created student profile
     * @throws IllegalArgumentException if the profile already exists or user is not found
     */
    public StudentProfile createProfile(String id, StudentProfileRequest request) {
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Profile already exists for user: " + id);
        }

        Users user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + id));

        StudentProfile profile = StudentProfile.builder()
            .id(id)
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(request.fullName())
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

    /**
     * Updates an existing student profile.
     *
     * @param id the user ID
     * @param request the updated profile data
     * @return the updated student profile
     * @throws IllegalArgumentException if the profile is not found
     */
    public StudentProfile updateProfile(String id, StudentProfileRequest request) {
        StudentProfile profile = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found for user: " + id));

        profile.setFullName(request.fullName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setEducationLevel(request.educationLevel());
        profile.setPreferredPlatform(request.preferredPlatform());
        profile.setPrimaryInterests(request.primaryInterests());
        profile.setLearningGoals(request.learningGoals());
        profile.setPreferredDifficultyLevel(request.preferredDifficultyLevel());
        profile.setHobbies(request.hobbies());

        return repository.save(profile);
    }

    /**
     * Checks if a profile exists (i.e., is completed) for the given user ID.
     *
     * @param id the user ID
     * @return true if profile exists, false otherwise
     */
    public boolean isProfileCompleted(String id) {
        return repository.existsById(id);
    }

    /**
     * Retrieves the student profile for a given user ID.
     *
     * @param id the user ID
     * @return the student profile
     * @throws IllegalArgumentException if the profile is not found
     */
    public StudentProfile getProfile(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found for user: " + id));
    }
}
