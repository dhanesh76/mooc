package com.dhanesh.auth.portal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.entity.SavedCourse;
import com.dhanesh.auth.portal.repository.CourseRepository;
import com.dhanesh.auth.portal.repository.SavedCourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedCourseService {
    private final SavedCourseRepository savedCourseRepository;
    private final CourseRepository courseRepository;

    //add course to saved 
    public SavedCourse saveCourse(String userId, String courseId){
        
        if (savedCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            throw new IllegalStateException("Course already saved");
        }

        SavedCourse course = SavedCourse
                                .builder()
                                .courseId(courseId)
                                .userId(userId)
                                .savedAt(LocalDateTime.now())
                                .build();

        courseRepository.findById(courseId).ifPresent(c -> {
            c.setSaveCount(c.getSaveCount() + 1);
            courseRepository.save(c);
        });

        return savedCourseRepository.save(course);
    }

    //remove course from saved 
    public void removeSavedCourse(String userId, String courseId){
        courseRepository.findById(courseId).ifPresent(c -> {
            c.setSaveCount(Math.max(0, c.getSaveCount() - 1));
            courseRepository.save(c);
        });

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

