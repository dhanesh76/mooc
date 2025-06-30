package com.dhanesh.auth.portal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("profile_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePhoto {
    @Id
    private String userId; // same as Users.id

    private byte[] photo;
    private String contentType;
}
