package com.dhanesh.auth.portal.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dhanesh.auth.portal.entity.Course;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    // Search Bar 
    List<Course> findByTitleContainingIgnoreCaseOrTutorContainingIgnoreCase(String title, String tutor);
}
 
