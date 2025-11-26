package com.evbuddy.ocpp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
}
