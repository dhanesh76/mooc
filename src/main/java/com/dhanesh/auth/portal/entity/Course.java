package com.dhanesh.auth.portal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    private String id;

    private String title;
    private String description;
    private List<String> tags;
    private String platform;
    private String tutor;
    private String difficultyLevel;
    private String duration;
    private double rating;
    private String language;
    private String url;
    private String imageUrl;
    private LocalDateTime lastUpdated;

    private long shareCount;
    private long saveCount;
}
