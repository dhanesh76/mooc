package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.StudentProfileRequest;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.StudentProfileService;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing student profiles.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    /**
     * Creates a new student profile for the authenticated user.
     *
     * @param principal the authenticated user principal
     * @param request the student profile data
     * @return created profile or error response
     */
    @PostMapping
    public ResponseEntity<?> createProfile(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody StudentProfileRequest request
    ) {
        try {
            StudentProfile profile = studentProfileService.createProfile(principal.getUser().getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Updates the student profile of the authenticated user.
     *
     * @param principal the authenticated user principal
     * @param request the updated profile data
     * @return updated profile or error response
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody StudentProfileRequest request
    ) {
        try {
            StudentProfile profile = studentProfileService.updateProfile(principal.getUser().getId(), request);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Checks whether the authenticated user's profile is completed.
     *
     * @param principal the authenticated user principal
     * @return true if profile is completed, false otherwise
     */
    @GetMapping("/status")
    public ResponseEntity<Boolean> checkProfileCompleted(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(studentProfileService.isProfileCompleted(principal.getUser().getId()));
    }

    /**
     * Retrieves the profile of the authenticated user.
     * Access is restricted if the profile is not marked as completed.
     *
     * @param principal the authenticated user principal
     * @return student profile or error response
     */
    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        String id = principal.getUser().getId();

        if (!studentProfileService.isProfileCompleted(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Complete Profile First");
        }

        StudentProfile profile = studentProfileService.getProfile(id);
        return ResponseEntity.ok(profile);
    }
}
