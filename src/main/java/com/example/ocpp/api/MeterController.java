package com.example.ocpp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import com.example.ocpp.dto.ApiResponse;
import com.example.ocpp.dto.MeterValueRequest;
import com.example.ocpp.domain.MeterValue;
import com.example.ocpp.domain.Transaction;
import com.example.ocpp.repo.TransactionRepo;
import com.example.ocpp.repo.MeterValueRepo;
import com.example.ocpp.websocket.EventPublisher;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class MeterController {
    private final TransactionRepo transactionRepo;
    private final MeterValueRepo meterValueRepo;
    private final EventPublisher events;

    /** POST /api/transactions/meter
     *  Body: { "transactionId":1, "valueWh": 12345, "timestamp": 1710000000000 }
     */
    @PostMapping("/meter")
    public ResponseEntity<ApiResponse> pushMeter(@Validated @RequestBody MeterValueRequest req) {
        log.info("Meter value for tx {} -> {} Wh", req.getTransactionId(), req.getValueWh());

        Transaction tx = transactionRepo.findById(req.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + req.getTransactionId()));

        MeterValue mv = MeterValue.builder()
                .transaction(tx)
                .valueWh(req.getValueWh())
                .timestamp(req.getTimestamp() == null ? Instant.now() : Instant.ofEpochMilli(req.getTimestamp()))
                .build();

        MeterValue saved = meterValueRepo.save(mv);

        events.sendMeter(tx.getId(), saved);

        return ResponseEntity.ok(ApiResponse.success("Meter value stored", saved));
    }
}
