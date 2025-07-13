package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.StudentProfileRequest;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.StudentProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing student profiles.
 */
@Tag(name = "Student Profile", description = "Endpoints for creating, updating, and fetching student profiles")
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    /**
     * Creates a new student profile for the authenticated user.
     */
    @Operation(summary = "Create student profile", description = "Creates a profile for the logged-in user.")
    @PostMapping
    public ResponseEntity<?> createProfile(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal,
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
     */
    @Operation(summary = "Update student profile", description = "Updates the profile of the authenticated user.")
    @PutMapping
    public ResponseEntity<?> updateProfile(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal,
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
     */
    @Operation(summary = "Check profile status", description = "Returns true if the user has completed their profile.")
    @GetMapping("/status")
    public ResponseEntity<Boolean> checkProfileCompleted(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal) {
        return ResponseEntity.ok(studentProfileService.isProfileCompleted(principal.getUser().getId()));
    }

    /**
     * Retrieves the profile of the authenticated user.
     * Restricted if profile is not yet completed.
     */
    @Operation(summary = "Get student profile", description = "Returns the user's profile if it has been completed.")
    @GetMapping
    public ResponseEntity<?> getProfile(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal) {

        String id = principal.getUser().getId();

        if (!studentProfileService.isProfileCompleted(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Complete Profile First");
        }

        StudentProfile profile = studentProfileService.getProfile(id);
        return ResponseEntity.ok(profile);
    }
}
