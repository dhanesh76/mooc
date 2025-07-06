package com.dhanesh.auth.portal.service;

import com.dhanesh.auth.portal.dto.FilterCourseRequest;
import com.dhanesh.auth.portal.entity.Course;
import com.dhanesh.auth.portal.entity.StudentProfile;
import com.dhanesh.auth.portal.repository.CourseRepository;
import com.dhanesh.auth.portal.repository.StudentProfileRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing course-related operations such as
 * CRUD, filtering, search, and personalized ranking.
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;
    private final StudentProfileRepository studentProfileRepository;

    /**
     * Adds a single new course to the repository.
     *
     * @param course the course to add
     * @return the saved course
     */
    public Course addCourse(Course course) {
        course.setLastUpdated(LocalDateTime.now());
        return courseRepository.save(course);
    }

    /**
     * Adds multiple courses in a batch operation.
     *
     * @param courses the list of courses
     * @return list of saved courses
     */
    public List<Course> addCourses(List<Course> courses) {
        courses.forEach(course -> course.setLastUpdated(LocalDateTime.now()));
        return courseRepository.saveAll(courses);
    }

    /**
     * Retrieves a paginated list of all courses.
     *
     * @param page the page number (0-indexed)
     * @param size the number of items per page
     * @return list of courses for that page
     */
    public List<Course> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findAll(pageable).getContent();
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id the course ID
     * @return optional containing the course if found
     */
    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    /**
     * Updates a course if it exists.
     *
     * @param id           the ID of the course to update
     * @param updatedData  new course data
     * @return updated course if found
     */
    public Optional<Course> updateCourse(String id, Course updatedData) {
        if (!courseRepository.existsById(id)) {
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

    /**
     * Deletes a course by ID.
     *
     * @param id the course ID to delete
     */
    public void deleteCourse(String id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }
        courseRepository.deleteById(id);
    }

    /**
     * Searches for courses by title or tutor name.
     *
     * @param query the search term
     * @return list of matching courses
     */
    public List<Course> searchCourses(String query) {
        return courseRepository.findByTitleContainingIgnoreCaseOrTutorContainingIgnoreCase(query, query);
    }

    /**
     * Filters courses based on criteria and ranks them using personalization.
     *
     * @param request the filter request object
     * @param userId  the student profile ID for personalization
     * @return list of personalized, filtered courses
     */
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

    /**
     * Ranks filtered courses based on personalization logic for a user.
     *
     * @param filteredCourses the filtered list of courses
     * @param userId          the user's profile ID
     * @return sorted course list by descending score
     */
    public List<Course> rankByScore(List<Course> filteredCourses, String userId) {
        StudentProfile profile = studentProfileRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return filteredCourses.stream()
            .map(course -> new AbstractMap.SimpleEntry<>(course, calculateScore(course, profile)))
            .sorted((a, b) -> {
                int cmp = Double.compare(b.getValue(), a.getValue());
                if (cmp == 0) {
                    // Tie-breaker: higher rating wins
                    return Double.compare(b.getKey().getRating(), a.getKey().getRating());
                }
                return cmp;
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }


    /**
     * Calculates a recommendation score for a course based on user preferences.
     *
     * @param course  the course to score
     * @param profile the student profile
     * @return score value for ranking
     */
    public double calculateScore(Course course, StudentProfile profile) {
        double score = 0.0;

        // Match tags with user's primary interests
        long tagMatch = course.getTags().stream()
            .filter(profile.getPrimaryInterests()::contains)
            .count();
        score += tagMatch * 2.0;

        // Match preferred difficulty level
        if (course.getDifficultyLevel().equalsIgnoreCase(profile.getPreferredDifficultyLevel())) {
            score += 3.0;
        }

        // Match preferred platform
        if (course.getPlatform().equalsIgnoreCase(profile.getPreferredPlatform())) {
            score += 2.0;
        }

        // Log-scaled influence of save & share counts
        score += Math.log1p(course.getSaveCount());
        score += Math.log1p(course.getShareCount());

        // Normalized rating (out of 5), scaled by 4
        score += (course.getRating() / 5.0) * 4.0;

        // Recency boost (last 30 days)
        if (course.getLastUpdated() != null &&
            course.getLastUpdated().isAfter(LocalDateTime.now().minusDays(30))) {
            score += 1.5;
        }

        return score;
    }

    /**
     * Increments the share count for a course (used for analytics or ranking).
     *
     * @param courseId the course ID
     */
    public void incrementShareCount(String courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setShareCount(course.getShareCount() + 1);
            courseRepository.save(course);
        });
    }
}
