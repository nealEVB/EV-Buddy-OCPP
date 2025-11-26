package com.evbuddy.ocpp.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.evbuddy.ocpp.domain.ChargingStation;

public interface ChargingStationRepo extends JpaRepository<ChargingStation, Long> {
    
    Optional<ChargingStation> findByStationId(String stationId);
    
    List<ChargingStation> findByStatus(ChargingStation.StationStatus status);
    
    List<ChargingStation> findByStationIdContaining(String stationId);
}
