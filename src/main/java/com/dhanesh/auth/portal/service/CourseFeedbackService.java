package com.dhanesh.auth.portal.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.feedback.CourseFeedbackRequest;
import com.dhanesh.auth.portal.dto.feedback.FeedbackSubmissionRequest;
import com.dhanesh.auth.portal.entity.CourseFeedback;
import com.dhanesh.auth.portal.repository.CourseFeedbackRepository;
import com.dhanesh.auth.portal.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class responsible for handling course feedback operations,
 * including submission, retrieval, and course rating updates.
 */
@Service
@RequiredArgsConstructor
public class CourseFeedbackService {

    private final CourseFeedbackRepository feedbackRepo;
    private final CourseRepository courseRepo;

    /**
     * Submits feedback for multiple courses and updates each course's rating.
     *
     * @param userId the ID of the user submitting feedback
     * @param request the feedback submission payload
     */
    public void submitFeedbacks(String userId, FeedbackSubmissionRequest request) {
        for (CourseFeedbackRequest cr : request.courseFeedbacks()) {
            CourseFeedback feedback = CourseFeedback.builder()
                .userId(userId)
                .courseId(cr.courseId())
                .rating(cr.rating())
                .enrolled(cr.enrolled())
                .comments(cr.comments())
                .submittedAt(Instant.now())
                .build();

            feedbackRepo.save(feedback);
            updateCourseRating(cr.courseId(), cr.rating());
        }
    }

    /**
     * Recalculates and updates the average rating for a course.
     *
     * @param courseId the course ID
     * @param newRating the newly submitted rating (used if it's the only one)
     */
    private void updateCourseRating(String courseId, int newRating) {
        List<CourseFeedback> allFeedback = feedbackRepo.findByCourseId(courseId);
        double average = allFeedback.stream()
            .mapToInt(CourseFeedback::getRating)
            .average()
            .orElse(newRating); // fallback if it's the first rating

        courseRepo.findById(courseId).ifPresent(course -> {
            course.setRating(average);
            courseRepo.save(course);
        });
    }

    /**
     * Retrieves all feedback submitted by a specific user.
     *
     * @param userId the user ID
     * @return list of feedback entries
     */
    public List<CourseFeedback> allFeedbacksByUserId(String userId){
        return feedbackRepo.findByUserId(userId);
    }

    /**
     * Retrieves all feedback entries for a specific course.
     *
     * @param courseId the course ID
     * @return list of feedback entries
     */
    public List<CourseFeedback> allFeedbackByCourseId(String courseId){
        return feedbackRepo.findByCourseId(courseId);
    }
}
