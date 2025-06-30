package com.dhanesh.auth.portal.controller;

import com.dhanesh.auth.portal.dto.StudentProfileRequest;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.service.StudentProfileService;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping("/{id}")
    public ResponseEntity<?> createProfile(
        @PathVariable String id,
        @Valid @RequestBody StudentProfileRequest request
    ) {
        try {
            StudentProfile profile = studentProfileService.createProfile(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
        @PathVariable String id,
        @Valid @RequestBody StudentProfileRequest request
    ) {
        try {
            StudentProfile profile = studentProfileService.updateProfile(id, request);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> checkProfileCompleted(@PathVariable String id) {
        return ResponseEntity.ok(studentProfileService.isProfileCompleted(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable String id) {
        if (!studentProfileService.isProfileCompleted(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Complete Profile First");
        }
        StudentProfile profile = studentProfileService.getProfile(id);
        return ResponseEntity.ok(profile);
    }
}
