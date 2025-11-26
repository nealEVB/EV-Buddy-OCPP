package com.evbuddy.ocpp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evbuddy.ocpp.domain.ChargingStation;
import com.evbuddy.ocpp.domain.Transaction;
import com.evbuddy.ocpp.domain.User;
import com.evbuddy.ocpp.domain.Transaction.State;
import com.evbuddy.ocpp.dto.SessionResponse;
import com.evbuddy.ocpp.dto.StartChargingRequest;
import com.evbuddy.ocpp.repo.ChargingStationRepo;
import com.evbuddy.ocpp.repo.ConnectorRepo;
import com.evbuddy.ocpp.repo.TransactionRepo;
import com.evbuddy.ocpp.repo.UserRepo;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargingSessionService {

    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final ChargingStationRepo chargingStationRepo;
    private final ConnectorRepo connectorRepo;
    private final OcppService ocppService;

    @Transactional
    public SessionResponse startCharging(String userEmail, StartChargingRequest request) {
        log.info("Starting charging session for user: {}", userEmail);

        // 1) user
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) station (your IDs are strings like EVSE-001, so don't parse to Long)
        ChargingStation station = chargingStationRepo.findByStationId(request.getChargingStationId())
                .orElseThrow(() -> new RuntimeException("Charging station not found"));

        // 3) (optional) verify connector exists on that station
        // if your repo supports it, otherwise skip
        connectorRepo.findByStationAndConnectorId(station, request.getConnectorNumber())
                .orElseThrow(() -> new RuntimeException("Connector not found"));

        // 4) send OCPP start
        Integer ocppTxId = ocppService.startRemoteTransaction(
                station.getStationId(),
                request.getConnectorNumber(),
                user.getEmail()
        );

        // 5) build and save Transaction using YOUR entity fields
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setStation(station);
        tx.setConnectorId(request.getConnectorNumber());
        tx.setTransactionIdOcpp(ocppTxId);
        tx.setIdTag(user.getEmail());            // or RFID/tag if you have it
        tx.setStartTime(Instant.now());
        tx.setState(State.STARTED);

        tx = transactionRepo.save(tx);

        log.info("Charging session started: {}", tx.getId());
        return mapToSessionResponse(tx);
    }

    @Transactional
    public SessionResponse stopCharging(String userEmail, String transactionId) {
        log.info("Stopping charging session: {}", transactionId);

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction tx = transactionRepo.findById(Long.valueOf(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!tx.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized to stop this session");
        }

        // call OCPP stop
        ocppService.stopRemoteTransaction(
                tx.getStation().getStationId(),
                tx.getTransactionIdOcpp()
        );

        // update tx
        tx.setStopTime(Instant.now());
        tx.setState(State.STOPPED);

        tx = transactionRepo.save(tx);

        return mapToSessionResponse(tx);
    }

    public List<SessionResponse> getUserSessions(String userEmail, String status) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> txs;
        if (status != null && !status.isBlank()) {
            // you already had this in your old code
            txs = transactionRepo.findByUserAndState(user, status);
        } else {
            txs = transactionRepo.findByUserIdOrderByStartTimeDesc(user.getUserId());
        }

        return txs.stream().map(this::mapToSessionResponse).collect(Collectors.toList());
    }

    public SessionResponse getSessionDetails(String userEmail, String transactionId) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction tx = transactionRepo.findById(Long.valueOf(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!tx.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access to this session");
        }

        return mapToSessionResponse(tx);
    }

    public List<SessionResponse> getActiveSessions(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> txs = transactionRepo.findByUserAndState(user, "STARTED");
        return txs.stream().map(this::mapToSessionResponse).collect(Collectors.toList());
    }

    private SessionResponse mapToSessionResponse(Transaction tx) {
        return SessionResponse.builder()
                .transactionId(tx.getId())
                .chargingStationId(tx.getStation() != null ? tx.getStation().getStationId() : null)
                .connectorNumber(tx.getConnectorId())
                .startTime(tx.getStartTime())
                .endTime(tx.getStopTime())
                .status(tx.getState() != null ? tx.getState().name() : null)
                .build();
    }
}
