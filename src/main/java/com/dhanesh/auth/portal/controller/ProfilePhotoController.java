package com.dhanesh.auth.portal.controller;

import java.io.IOException;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.repository.UserRepository;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.ProfilePhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling user profile photo upload and retrieval.
 */
@Tag(name = "User Profile", description = "Upload and view user profile photo")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile-photo")
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;
    private final UserRepository userRepository;

    /**
     * Uploads a profile photo (JPEG/PNG) for the authenticated user.
     *
     * @param principal the authenticated user's principal
     * @param photo the image file to upload
     * @return success message if uploaded
     * @throws IOException
     * @throws IllegalAccessException
     */
    @Operation(
        summary = "Upload profile photo",
        description = "Allows authenticated user to upload their profile photo (PNG/JPEG only)"
    )
    @PutMapping
    public ResponseEntity<?> uploadPhoto(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal,

        @RequestParam("file")
        @Parameter(description = "PNG or JPEG image file") MultipartFile photo
    ) throws IOException, IllegalAccessException {
        profilePhotoService.uploadPhoto(principal.getUser().getId(), photo);
        return ResponseEntity.ok("Photo uploaded successfully");
    }

    /**
     * Retrieves the current user's profile photo.
     *
     * @param principal the authenticated user
     * @return image as byte[] with content-type header
     */
    @Operation(
        summary = "Get profile photo",
        description = "Fetches the stored profile photo of the logged-in user"
    )
    @GetMapping
    public ResponseEntity<byte[]> getProfilePhoto(
        @AuthenticationPrincipal
        @Parameter(description = "Authenticated user") UserPrincipal principal
    ) {
        Users user = userRepository
            .findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok()
            .header("Content-Type", profilePhotoService.getProfilePhotoContentType(user.getId()))
            .body(profilePhotoService.getProfilePhoto(user.getId()));
    }
}
