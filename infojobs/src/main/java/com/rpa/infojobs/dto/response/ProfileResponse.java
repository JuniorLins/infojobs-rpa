package com.rpa.infojobs.dto.response;

import com.rpa.infojobs.model.User;

import java.time.LocalDateTime;

public record ProfileResponse(

        String id,
        String email,
        String name,
        String role,
        boolean active,
        String infojobsEmail,
        LocalDateTime createdAt,
        LocalDateTime lastLogin
) {

    public static ProfileResponse from(User u) {
        return new ProfileResponse(
                u.getId(), u.getEmail(), u.getName(),
                u.getRole().name(), u.isActive(), u.getInfojobsEmail(),
                u.getCreatedAt(), u.getLastLogin()
        );
    }
}
