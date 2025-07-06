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

@Service
@RequiredArgsConstructor
public class CourseFeedbackService {

    private final CourseFeedbackRepository feedbackRepo;
    private final CourseRepository courseRepo;

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

    private void updateCourseRating(String courseId, int newRating) {
        List<CourseFeedback> allFeedback = feedbackRepo.findByCourseId(courseId);
        double average = allFeedback.stream()
            .mapToInt(CourseFeedback::getRating)
            .average()
            .orElse(newRating); // fallback for first rating

        courseRepo.findById(courseId).ifPresent(course -> {
            course.setRating(average);
            courseRepo.save(course);
        });
    }

    public List<CourseFeedback> allFeedbacksByUserId(String userId){
        return feedbackRepo.findByUserId(userId);
    }
    
    public List<CourseFeedback> allFeedbackByCourseId(String courseId){
        return feedbackRepo.findByCourseId(courseId);
    }
}
