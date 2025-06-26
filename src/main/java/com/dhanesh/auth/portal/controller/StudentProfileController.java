package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.StudentProfileRequest;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping("/{userId}")
    public ResponseEntity<StudentProfile> createOrUpdateProfile(
        @PathVariable String userId,
        @Valid @RequestBody StudentProfileRequest request
    ) {
        StudentProfile profile = studentProfileService.createOrUpdateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Boolean> checkProfileCompleted(@PathVariable String userId) {
        return ResponseEntity.ok(studentProfileService.isProfileCompleted(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<StudentProfile> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(studentProfileService.getProfile(userId));
    }
}
