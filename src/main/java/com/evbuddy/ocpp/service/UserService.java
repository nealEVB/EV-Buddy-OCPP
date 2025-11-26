package com.evbuddy.ocpp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evbuddy.ocpp.domain.User;
import com.evbuddy.ocpp.dto.UpdateProfileRequest;
import com.evbuddy.ocpp.dto.UserProfileResponse;
import com.evbuddy.ocpp.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private  UserRepo userRepo;
    
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    @Transactional
    public void updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        userRepo.save(user);
        log.info("Profile updated for user: {}", user.getUserId());
    }
}
