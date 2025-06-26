package com.dhanesh.auth.portal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.entity.SavedCourse;
import com.dhanesh.auth.portal.repository.SavedCourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedCourseService {
    private final SavedCourseRepository savedCourseRepository;
    
    //add course to saved 
    public SavedCourse saveCourse(String userId, String courseId){
        
        if (savedCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            throw new IllegalStateException("Course already saved");
        }

        SavedCourse course = SavedCourse
                                .builder()
                                .courseId(courseId)
                                .userId(userId)
                                .build();

        return savedCourseRepository.save(course);
    }

    //remove course from saved 
    public void removeSavedCourse(String userId, String courseId){
        savedCourseRepository.deleteByUserIdAndCourseId(userId, courseId);
    }

    //check is course saved 
    public boolean isCourseSaved(String userId, String courseId){
        return savedCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
    }

    //get courses saved 
    public List<SavedCourse> getSavedCourses(String userId){
        return savedCourseRepository.findByUserId(userId);
    }

    public long getSavedCourseCount(String userId) {
        return savedCourseRepository.countByUserId(userId);
    }

}
