package com.dhanesh.auth.portal.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dhanesh.auth.portal.entity.ProfilePhoto;

@Repository
public interface ProfilePhotoRepository extends MongoRepository<ProfilePhoto, String> {
}
