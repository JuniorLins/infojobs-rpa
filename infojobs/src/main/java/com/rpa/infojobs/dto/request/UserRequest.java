package com.rpa.infojobs.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String name;

    @NotBlank
    private String infojobsEmail;

    @NotBlank
    private String infojobsPassword;

    private String role;
}
