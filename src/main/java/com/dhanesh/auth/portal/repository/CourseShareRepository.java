package com.dhanesh.auth.portal.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dhanesh.auth.portal.entity.CourseShare;

@Repository
public interface CourseShareRepository extends MongoRepository<CourseShare, String> {
    List<CourseShare> findByCourseId(String courseId);

    void deleteByUserId(String id);
}
