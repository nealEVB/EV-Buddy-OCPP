package com.evbuddy.ocpp.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // <-- Spring's annotation
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evbuddy.ocpp.dto.SessionResponse;
import com.evbuddy.ocpp.dto.StartChargingRequest;
import com.evbuddy.ocpp.service.ChargingSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SessionController {

    // let Lombok create the constructor for this
    private final ChargingSessionService sessionService;

    @PostMapping("/start")
    public ResponseEntity<SessionResponse> startCharging(
            @Valid @RequestBody StartChargingRequest request,
            Authentication authentication) {

        String userEmail = extractUserEmail(authentication);
        log.info("Start charging request from user: {}", userEmail);

        SessionResponse response = sessionService.startCharging(userEmail, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/stop")
    public ResponseEntity<SessionResponse> stopCharging(
            @PathVariable String sessionId,
            Authentication authentication) {

        String userEmail = extractUserEmail(authentication);
        log.info("Stop charging request for session: {}", sessionId);

        SessionResponse response = sessionService.stopCharging(userEmail, sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getUserSessions(
            @RequestParam(required = false) String status,
            Authentication authentication) {

        String userEmail = extractUserEmail(authentication);
        log.info("Get sessions for user: {}, status: {}", userEmail, status);

        List<SessionResponse> sessions = sessionService.getUserSessions(userEmail, status);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSessionDetails(
            @PathVariable String sessionId,
            Authentication authentication) {

        String userEmail = extractUserEmail(authentication);
        log.info("Get session details: {}", sessionId);

        SessionResponse session = sessionService.getSessionDetails(userEmail, sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SessionResponse>> getActiveSessions(Authentication authentication) {
        String userEmail = extractUserEmail(authentication);
        log.info("Get active sessions for user: {}", userEmail);

        List<SessionResponse> sessions = sessionService.getActiveSessions(userEmail);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Central place to handle "authentication can be null".
     * You can swap this to throw 403/401 in your GlobalExceptionHandler if you want.
     */
    private String extractUserEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // you can throw your custom exception here instead
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return authentication.getName();
    }
}
