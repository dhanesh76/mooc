package com.dhanesh.auth.portal.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("course_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseFeedback {

    @Id
    private String id;

    private String userId;
    private String courseId;

    @Min(1) @Max(5)
    private int rating;

    private boolean enrolled;
    private String comments;
    private Instant submittedAt;


    private boolean isAnonymous;
}
