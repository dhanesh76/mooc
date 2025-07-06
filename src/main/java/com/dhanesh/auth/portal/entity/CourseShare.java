package com.dhanesh.auth.portal.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("course_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseShare {

    @Id
    private String id;

    private String courseId;
    private String userId;         // Optional: null if guest
    private String platform;       // Optional (e.g., WhatsApp, Twitter)

    private Instant sharedAt;
}

