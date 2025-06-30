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
import com.dhanesh.auth.portal.service.ProfilePhototService;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/profile-photo")
public class ProfilePhototController {
    
    private final ProfilePhototService profilePhotoService;
    private final UserRepository userRepository;

    @PutMapping
    public ResponseEntity<?> uploadPhoto(@AuthenticationPrincipal UserDetails userDetails, 
        @RequestParam("file") MultipartFile photo
    ) throws IOException, IllegalAccessException {
        
        Users user = userRepository
            .findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        profilePhotoService.uploadPhoto(user.getId(), photo);
        return ResponseEntity.ok("Photo uploaded successfully");
    }

    @GetMapping
    public ResponseEntity<byte[]> getProfilePhoto(@AuthenticationPrincipal UserDetails userDetails) {
        
        Users user = userRepository
            .findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok()
            .header("Content-Type", profilePhotoService.getProfilePhotoContentType(user.getId()))
            .body(profilePhotoService.getProfilePhoto(user.getId()));
    }    
}
