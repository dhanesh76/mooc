package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.repository.CourseRepository;
import com.dhanesh.auth.portal.repository.StudentProfileRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;
    private final StudentProfileRepository studentProfileRepository;

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
        if(!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }       
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
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }
        courseRepository.deleteById(id);
    }

    //  Search by title or tutor
    public List<Course> searchCourses(String query) {
        return courseRepository.findByTitleContainingIgnoreCaseOrTutorContainingIgnoreCase(query, query);
    }

    //  Filter courses (tags, platform, difficulty)
    public List<Course> filterCourses(FilterCourseRequest request, String userId) {
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
            criteria.and("duration").is(request.duration());
        }

        Query query = new Query(criteria);
        List<Course> filteredCourses = mongoTemplate.find(query, Course.class);

        return rankByScore(filteredCourses, userId);
    }

    public List<Course> rankByScore(List<Course> filteredCourses, String userId) {
        StudentProfile profile = studentProfileRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return filteredCourses.stream()
            .map(course -> new AbstractMap.SimpleEntry<>(course, calculateScore(course, profile)))
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public double calculateScore(Course course, StudentProfile profile) {
        double score = 0.0;

        long tagMatch = course.getTags().stream()
            .filter(profile.getPrimaryInterests()::contains)
            .count();
        score += tagMatch * 2.0;

        if (course.getDifficultyLevel().equalsIgnoreCase(profile.getPreferredDifficultyLevel())) {
            score += 3.0;
        }

        if (course.getPlatform().equalsIgnoreCase(profile.getPreferredPlatform())) {
            score += 2.0;
        }

        score += Math.log1p(course.getSaveCount());
        score += Math.log1p(course.getShareCount());
        score += course.getRating();

        if (course.getLastUpdated() != null &&
            course.getLastUpdated().isAfter(LocalDateTime.now().minusDays(30))) {
            score += 1.5;
        }

        return score;
    }


    public void incrementShareCount(String courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setShareCount(course.getShareCount() + 1);
            courseRepository.save(course);
        });
    }
}
