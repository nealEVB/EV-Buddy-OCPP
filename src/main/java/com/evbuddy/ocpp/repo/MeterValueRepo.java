package com.evbuddy.ocpp.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evbuddy.ocpp.domain.MeterValue;

@Repository
public interface MeterValueRepo extends JpaRepository<MeterValue, Long> {
    List<MeterValue> findByTransaction_IdOrderByTimestampAsc(Long transactionId);
}
