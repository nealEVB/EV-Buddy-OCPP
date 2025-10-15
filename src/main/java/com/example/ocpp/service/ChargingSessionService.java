package com.example.ocpp.service;

import com.example.ocpp.domain.ChargingStation;
import com.example.ocpp.domain.Connector;
import com.example.ocpp.domain.Transaction;
import com.example.ocpp.domain.User;

import com.example.ocpp.dto.SessionResponse;
import com.example.ocpp.dto.StartChargingRequest;
import com.example.ocpp.repo.ChargingStationRepo;
import com.example.ocpp.repo.ConnectorRepo;
import com.example.ocpp.repo.TransactionRepo;
import com.example.ocpp.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargingSessionService {
    @Autowired
    private  TransactionRepo transactionRepo;
    private  UserRepo userRepo;
    private  ChargingStationRepo chargingStationRepo;
    private  ConnectorRepo connectorRepo;
    private  OcppService ocppService;
    
    @Transactional
    public SessionResponse startCharging(String userEmail, StartChargingRequest request) {
        log.info("Starting charging session for user: {}", userEmail);
        
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChargingStation station = chargingStationRepo.findById(Long.valueOf(request.getChargingStationId()))
                .orElseThrow(() -> new RuntimeException("Charging station not found"));
        
        Connector connector = connectorRepo.findByStationAndConnectorId(
                station, 
                request.getConnectorNumber()
        ).orElseThrow(() -> new RuntimeException("Connector not found"));
        
        // Send OCPP command via your existing service
        Integer transactionId = ocppService.startRemoteTransaction(
                station.getStationId(), 
                request.getConnectorNumber(), 
                user.getEmail()
        );
        
        // Create transaction (adjust fields based on YOUR Transaction entity)
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        // Set other fields based on your Transaction entity structure
        // transaction.setChargingStation(station);
        // transaction.setConnector(connector);
        // transaction.setStartTime(LocalDateTime.now());
        // transaction.setStatus("ACTIVE");
        // transaction.setTransactionId(transactionId);
        
        transaction = transactionRepo.save(transaction);
        
        log.info("Charging session started: {}", transaction.getId());
        return mapToSessionResponse(transaction);
    }
    
    @Transactional
    public SessionResponse stopCharging(String userEmail, String transactionId) {
        log.info("Stopping charging session: {}", transactionId);
        
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Transaction transaction = transactionRepo.findById(Long.valueOf(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized to stop this session");
        }
        
        // Send OCPP stop command
        ocppService.stopRemoteTransaction(
                transaction.getStation().getStationId(),
                transaction.getTransactionIdOcpp()
        );
        
        // Update transaction status
        // transaction.setEndTime(LocalDateTime.now());
        // transaction.setStatus("COMPLETED");
        
        transaction = transactionRepo.save(transaction);
        
        log.info("Charging session stopped: {}", transactionId);
        return mapToSessionResponse(transaction);
    }
    
    public List<SessionResponse> getUserSessions(String userEmail, String status) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Transaction> transactions;
        
        if (status != null && !status.isEmpty()) {
            transactions = transactionRepo.findByUserAndState(user, status);
        } else {
            transactions = transactionRepo.findByUserIdOrderByStartTimeDesc(user.getUserId());
        }
        
        return transactions.stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }
    
    public SessionResponse getSessionDetails(String userEmail, String transactionId) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Transaction transaction = transactionRepo.findById(Long.valueOf(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access to this session");
        }
        
        return mapToSessionResponse(transaction);
    }
    
    public List<SessionResponse> getActiveSessions(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Transaction> transactions = transactionRepo.findByUserAndState(user, "ACTIVE");
        
        return transactions.stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }
    
    private SessionResponse mapToSessionResponse(Transaction transaction) {
        // Adjust this mapping based on YOUR Transaction entity fields
        Long duration = null;
        // if (transaction.getEndTime() != null && transaction.getStartTime() != null) {
        //     duration = Duration.between(transaction.getStartTime(), transaction.getEndTime()).toMinutes();
        // }
        
        return SessionResponse.builder()
                .transactionId(transaction.getId())
                // Map other fields from your Transaction entity
                // .chargingStationId(transaction.getChargingStation().getStationId())
                // .stationName(transaction.getChargingStation().getName())
                // .connectorNumber(transaction.getConnector().getConnectorNumber())
                // .startTime(transaction.getStartTime())
                // .endTime(transaction.getEndTime())
                .durationMinutes(duration)
                // .energyConsumedKwh(transaction.getEnergyConsumed())
                // .cost(transaction.getCost())
                // .status(transaction.getStatus())
                .build();
    }
}