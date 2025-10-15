package com.example.ocpp.api;

import com.example.ocpp.domain.Transaction;
import com.example.ocpp.dto.ApiResponse;
import com.example.ocpp.dto.SessionSummaryResponse;
import com.example.ocpp.repo.TransactionRepo;
import com.example.ocpp.service.SessionSummaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
//@ConditionalOnProperty(name = "database.enabled", havingValue = "true", matchIfMissing = false)
public class ReportController {
	@Autowired
    private TransactionRepo txRepo;

	 private final SessionSummaryService summaryService;

	    /** GET /api/reports/session-summary/{transactionId} */
	    @GetMapping("/session-summary/{transactionId}")
	    public ResponseEntity<ApiResponse> summary(@PathVariable Long transactionId) {
	        SessionSummaryResponse summary = summaryService.summarize(transactionId);
	        return ResponseEntity.ok(ApiResponse.success("Session summary", summary));
	    }
    @GetMapping("/energy")
    public Map<String,Object> energy(@RequestParam String from, @RequestParam String to){
        Instant f = Instant.parse(from); Instant t = Instant.parse(to);
        List<Transaction> txs = txRepo.findByStartTimeBetween(f,t);
        double kWh = txs.stream()
            .filter(tx -> tx.getStopMeterWh()!=null && tx.getStartMeterWh()!=null)
            .mapToDouble(tx -> (tx.getStopMeterWh() - tx.getStartMeterWh())/1000.0)
            .sum();
        Map<String,Object> res = new HashMap<>();
        res.put("totalKWh", kWh);
        res.put("count", txs.size());
        return res;
    }
}
