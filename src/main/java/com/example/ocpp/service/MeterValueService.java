package com.example.ocpp.service;

import com.example.ocpp.domain.MeterValue;
import com.example.ocpp.domain.Transaction;
import com.example.ocpp.repo.MeterValueRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeterValueService {

    private final MeterValueRepo meterValueRepo;

    public MeterValue record(Transaction tx, double energyKWh) {
        MeterValue mv = MeterValue.builder()
                .transaction(tx)
                .energyKWh(energyKWh)
                .timestamp(Instant.now())
                .build();
        return meterValueRepo.save(mv);
    }

    public List<MeterValue> getValues(Long transactionId) {
        return meterValueRepo.findByTransaction_IdOrderByTimestampAsc(transactionId);
    }
}

