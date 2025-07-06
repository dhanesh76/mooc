package com.dhanesh.auth.portal.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.dhanesh.auth.portal.entity.Users;
import com.dhanesh.auth.portal.repository.UserRepository;
import com.dhanesh.auth.portal.security.userdetails.UserPrincipal;
import com.dhanesh.auth.portal.service.ProfilePhotoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/profile-photo")
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;
    private final UserRepository userRepository;

    /**
     * Uploads a profile photo for the authenticated user.
     * Accepts only PNG or JPEG.
     *
     * @param principal the authenticated user's principal
     * @param photo     the image file to upload
     */
    @PutMapping
    public ResponseEntity<?> uploadPhoto(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam("file") MultipartFile photo
    ) throws IOException, IllegalAccessException {
        
        profilePhotoService.uploadPhoto(principal.getUser().getId(), photo);
        return ResponseEntity.ok("Photo uploaded successfully");
    }

    /**
     * Fetches the profile photo of the authenticated user.
     *
     * @param userDetails the Spring Security user
     * @return image bytes with correct content type
     */
    @GetMapping
    public ResponseEntity<byte[]> getProfilePhoto(@AuthenticationPrincipal UserPrincipal principal) {
        Users user = userRepository
            .findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok()
            .header("Content-Type", profilePhotoService.getProfilePhotoContentType(user.getId()))
            .body(profilePhotoService.getProfilePhoto(user.getId()));
    }
}
