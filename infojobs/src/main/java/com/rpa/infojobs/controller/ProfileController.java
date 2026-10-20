package com.rpa.infojobs.controller;

import com.rpa.infojobs.dto.response.ProfileResponse;
import com.rpa.infojobs.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me() {
        return ResponseEntity.ok(ProfileResponse.from(SecurityUtils.getCurrentUser()));
    }
}
