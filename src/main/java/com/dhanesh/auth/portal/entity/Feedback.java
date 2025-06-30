package com.dhanesh.auth.portal.entity;

import lombok.*;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    private String id;

    private String userId;

    private int recommendationRating;         // 1–5
    private String relevance;                 // "Very relevant", etc.
    private boolean enrolled;

    private Integer courseQualityRating;      // Nullable, required only if enrolled == true

    String feedback;        //long text 

    String appExperience;    //Awseome, Good, Need Improvement 

    Instant createdAt;         // Timestamp of feedback creation
}
