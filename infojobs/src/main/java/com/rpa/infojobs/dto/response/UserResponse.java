package com.rpa.infojobs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String name;
    private String infojobsEmail;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}
