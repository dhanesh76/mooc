package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;

    //  Add a single course
    public Course addCourse(Course course) {
        course.setLastUpdated(LocalDateTime.now());

        return courseRepository.save(course);
    }

    //  Add multiple courses
    public List<Course> addCourses(List<Course> courses) {
        courses.forEach(course -> course.setLastUpdated(LocalDateTime.now()));
        return courseRepository.saveAll(courses);
    }

    //  Get all courses
    public List<Course> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findAll(pageable).getContent();
    }

    //  Get course by ID
    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    //  Update a course
    public Optional<Course> updateCourse(String id, Course updatedData) {
         return courseRepository.findById(id).map(existing -> {
            existing.setTitle(updatedData.getTitle());
            existing.setDescription(updatedData.getDescription());
            existing.setTags(updatedData.getTags());
            existing.setPlatform(updatedData.getPlatform());
            existing.setTutor(updatedData.getTutor());
            existing.setDifficultyLevel(updatedData.getDifficultyLevel());
            existing.setDuration(updatedData.getDuration());
            existing.setRating(updatedData.getRating());
            existing.setLanguage(updatedData.getLanguage());
            existing.setUrl(updatedData.getUrl());
            existing.setImageUrl(updatedData.getImageUrl());
            existing.setLastUpdated(LocalDateTime.now());
            return courseRepository.save(existing);
        });
    }

    //  Delete a course
    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    //  Search by title or tutor
    public List<Course> searchCourses(String query) {
        return courseRepository.findByTitleContainingIgnoreCaseOrTutorContainingIgnoreCase(query, query);
    }

    //  Filter courses (tags, platform, difficulty)
    public List<Course> filterCourses(FilterCourseRequest request) {
        Criteria criteria = new Criteria();

        if (request.tags() != null && !request.tags().isEmpty()) {
            criteria.and("tags").in(request.tags());
        }

        if (request.platforms() != null && !request.platforms().isEmpty()) {
            criteria.and("platform").in(request.platforms());
        }

        if (request.difficulty() != null) {
            criteria.and("difficultyLevel").is(request.difficulty());
        }

        if (request.duration() != null && !request.duration().isBlank()) {
            criteria.and("duration").is(request.duration()); // 🔹 Add duration filter
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Course.class);
    }
}
