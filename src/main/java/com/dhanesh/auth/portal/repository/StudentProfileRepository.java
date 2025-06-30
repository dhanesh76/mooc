package com.dhanesh.auth.portal.repository;

import com.dhanesh.auth.portal.entity.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentProfileRepository extends MongoRepository<StudentProfile, String> {
}
