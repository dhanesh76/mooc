package com.dhanesh.auth.portal.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dhanesh.auth.portal.entity.CourseFeedback;

@Repository
public interface CourseFeedbackRepository extends MongoRepository<CourseFeedback, String> {
    List<CourseFeedback> findByCourseId(String courseId);
    List<CourseFeedback> findByUserId(String userId);
}
