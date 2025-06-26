package com.dhanesh.auth.portal.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("saved-course")
public class SavedCourse {
    @Id
    String id;
     
    String courseId;    //FK - Course 
    String userId;      //FK - User 

    LocalDateTime savedAt;
}