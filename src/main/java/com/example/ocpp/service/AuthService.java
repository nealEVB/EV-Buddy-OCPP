package com.example.ocpp.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ocpp.domain.RefreshToken;
import com.example.ocpp.domain.User;
import com.example.ocpp.dto.AuthResponse;
import com.example.ocpp.dto.LoginRequest;
import com.example.ocpp.dto.RegisterRequest;
import com.example.ocpp.repo.RefreshTokenRepo;
import com.example.ocpp.repo.UserRepo;
import com.example.ocpp.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor // ✅ will inject all final fields automatically
public class AuthService {

    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(User.UserRole.valueOf(request.getRole().toUpperCase()))
                .isActive(true)
                .build();

        user = userRepo.save(user);
        log.info("User registered successfully: {}", user.getUserId());

        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        log.info("User logged in successfully: {}", user.getUserId());

        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");

        RefreshToken tokenEntity = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(tokenEntity);
            throw new RuntimeException("Refresh token expired");
        }

        User user = tokenEntity.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        refreshTokenRepo.delete(tokenEntity);
        saveRefreshToken(user, newRefreshToken);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        try {
            String userId = jwtUtil.extractUserId(accessToken);
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            refreshTokenRepo.deleteByUser(user);
            log.info("User logged out: {}", userId);
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepo.save(refreshToken);
    }
}
