package com.example.ocpp.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final Map<Long, Map<String, Object>> activeTransactions = new HashMap<>();
    private long transactionCounter = 1L;

    // ✅ Start Transaction
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startTransaction(@RequestBody Map<String, Object> request) {
        Long id = transactionCounter++;
        request.put("transactionId", id);
        request.put("startTime", Instant.now());
        activeTransactions.put(id, request);
        return ResponseEntity.ok(request);
    }

    // ✅ Stop Transaction
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopTransaction(@RequestBody Map<String, Object> request) {
        Long id = ((Number) request.get("transactionId")).longValue();
        Map<String, Object> txn = activeTransactions.get(id);
        if (txn == null) return ResponseEntity.notFound().build();
        txn.put("endTime", Instant.now());
        return ResponseEntity.ok(txn);
    }
}
