package com.evbuddy.ocpp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.evbuddy.ocpp.domain.MeterValue;
import com.evbuddy.ocpp.domain.Transaction;
import com.evbuddy.ocpp.dto.SessionSummaryResponse;
import com.evbuddy.ocpp.repo.MeterValueRepo;
import com.evbuddy.ocpp.repo.TransactionRepo;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionSummaryService {

    private final TransactionRepo transactionRepo;
    private final MeterValueRepo meterValueRepo;

    // Optional cost calculator (set a value to enable pricing)
    private final Double pricePerKWh = null;

    // ✅ This is the method your controller expects
    public SessionSummaryResponse summarize(Long txId) {
        Transaction tx = transactionRepo.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + txId));

        List<MeterValue> values = meterValueRepo.findByTransaction_IdOrderByTimestampAsc(txId);

        long startWh = 0L;
        long endWh   = 0L;
        if (!values.isEmpty()) {
            startWh = values.get(0).getValueWh();
            endWh   = values.get(values.size() - 1).getValueWh();
        }
        long energyWh = Math.max(0L, endWh - startWh);

        Instant start = tx.getStartTime();
        Instant end   = tx.getEndTime() != null ? tx.getEndTime() : Instant.now();
        long durationSec = Math.max(0L, end.getEpochSecond() - start.getEpochSecond());

        Double cost = null;
        if (pricePerKWh != null) {
            cost = (energyWh / 1000.0) * pricePerKWh;
        }

        return SessionSummaryResponse.builder()
                .transactionId(tx.getId())
                .stationId(tx.getStation().getStationId())
                .connectorId(tx.getConnectorId())
                .tokenId(tx.getTokenId())
                .startTime(start)
                .endTime(tx.getEndTime())
                .durationSeconds(durationSec)
                .startWh(startWh)
                .endWh(endWh)
                .energyWh(energyWh)
                .cost(cost)
                .build();
    }
}
