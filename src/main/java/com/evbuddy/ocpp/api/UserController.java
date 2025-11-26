package com.evbuddy.ocpp.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // <-- Spring's
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

import com.evbuddy.ocpp.dto.ApiResponse;
import com.evbuddy.ocpp.dto.UpdateProfileRequest;
import com.evbuddy.ocpp.dto.UserProfileResponse;
import com.evbuddy.ocpp.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    // let Lombok inject this
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String email = extractEmail(authentication);
        log.info("Profile request for user: {}", email);

        UserProfileResponse profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        String email = extractEmail(authentication);
        log.info("Profile update request for user: {}", email);

        userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully"));
    }

    private String extractEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return authentication.getName();
    }
}
