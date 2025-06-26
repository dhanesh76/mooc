package com.dhanesh.auth.portal.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users") // This replaces @Entity and @Table
public class Users {

    @Id
    private String id;  // MongoDB uses String-based ObjectId

    private String username;

    private String password;

    private String email;

    private String role;

    private boolean verified = false;

    @CreatedDate
    private Instant createdAt;

    private AuthProvider authProvider;
}
