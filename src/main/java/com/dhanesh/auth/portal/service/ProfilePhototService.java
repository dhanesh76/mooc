package com.dhanesh.auth.portal.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dhanesh.auth.portal.entity.ProfilePhoto;
import com.dhanesh.auth.portal.repository.ProfilePhotoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfilePhototService {

    private final ProfilePhotoRepository profilePhotoRepository;

    public void uploadPhoto(String id, MultipartFile photo) throws IllegalAccessException, IOException {
        if (photo.isEmpty()) {
            throw new IllegalAccessException("Photo can't be empty");
        }

        String contentType = photo.getContentType();
        if (!(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Only PNG and JPEG formats are supported");
        }

        ProfilePhoto profilePhoto = profilePhotoRepository
            .findById(id)
            .orElse(new ProfilePhoto());

        profilePhoto.setUserId(id);
        profilePhoto.setPhoto(photo.getBytes());
        profilePhoto.setContentType(contentType); // ✅ Store content type

        profilePhotoRepository.save(profilePhoto);
    }

    public String getProfilePhotoContentType(String id) {
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile photo not found"));
        return profilePhoto.getContentType();
    }

    public byte[] getProfilePhoto(String id) {
        ProfilePhoto profilePhoto = profilePhotoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile photo not found"));
        return profilePhoto.getPhoto();
    }
}
