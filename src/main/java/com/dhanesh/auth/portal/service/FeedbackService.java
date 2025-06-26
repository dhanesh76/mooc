package com.dhanesh.auth.portal.service;

import org.springframework.stereotype.Service;

import com.dhanesh.auth.portal.dto.FeedbackRequest;
import com.dhanesh.auth.portal.entity.Feedback;
import com.dhanesh.auth.portal.repository.FeedbackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(FeedbackRequest request){
        Feedback feedback = Feedback
            .builder()
            .userId(request.userId())
            .recommendationRating(request.recommendationRating())

            .relevance(request.relevance())
            .enrolled(request.enrolled())
            
            .courseQualityRating(request.courseQualityRating())
            .feedback(request.feedback())
            .appExperience(request.appExperience())
            .build();
    
            return feedbackRepository.save(feedback);
    }
}
