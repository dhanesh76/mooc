package com.dhanesh.auth.portal.repository;

import com.dhanesh.auth.portal.entity.SavedCourse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedCourseRepository extends MongoRepository<SavedCourse, String> {

    // Find all saved courses for a user
    List<SavedCourse> findByUserId(String userId);

    // Check if course is saved (returns Optional)
    Optional<SavedCourse> findByUserIdAndCourseId(String userId, String courseId);

    // Delete saved course by user + course
    void deleteByUserIdAndCourseId(String userId, String courseId);

    //get the count of the courses saved by the user 
    long countByUserId(String userId);
}
