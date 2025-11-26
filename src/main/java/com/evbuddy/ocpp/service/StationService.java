package com.evbuddy.ocpp.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.evbuddy.ocpp.domain.ChargingStation;
import com.evbuddy.ocpp.domain.Connector;
import com.evbuddy.ocpp.dto.StationResponse;
import com.evbuddy.ocpp.repo.ChargingStationRepo;
import com.evbuddy.ocpp.repo.ConnectorRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {
	private final ChargingStationRepo stationRepo;
    private final ConnectorRepo connectorRepo;
    
    public List<StationResponse> getAllStations(String status, String stationIdPart) {
        List<ChargingStation> stations;

        // Case 1: Filter by status (Enum)
        if (status != null && !status.isEmpty()) {
            try {
                ChargingStation.StationStatus enumStatus = ChargingStation.StationStatus.valueOf(status);
                stations = stationRepo.findByStatus(enumStatus);
            } catch (IllegalArgumentException e) {
                // Invalid enum string
                throw new IllegalArgumentException("Invalid station status: " + status);
            }
        }
        // Case 2: Filter by station ID substring
        else if (stationIdPart != null && !stationIdPart.isEmpty()) {
            stations = stationRepo.findByStationIdContaining(stationIdPart);
        }
        // Case 3: Get all
        else {
            stations = stationRepo.findAll();
        }

        return stations.stream()
                .map(this::mapToStationResponse)
                .collect(Collectors.toList());
    }
    
    public StationResponse getStationDetails(String stationId) {
        ChargingStation station = stationRepo.findById(Long.valueOf(stationId))
                .orElseThrow(() -> new RuntimeException("Charging station not found"));
        
        return mapToStationResponse(station);
    }
    
    public boolean checkAvailability(String stationId) {
        ChargingStation station = stationRepo.findById(Long.valueOf(stationId))
                .orElseThrow(() -> new RuntimeException("Charging station not found"));
        
        List<Connector> connectors = connectorRepo.findByStation(station);
        
        // Adjust status check based on your entity field names
        return "AVAILABLE".equals(station.getStatus()) &&
                connectors.stream().anyMatch(c -> "AVAILABLE".equals(c.getStatus()));
    }
    
    private StationResponse mapToStationResponse(ChargingStation station) {
        List<Connector> connectors = connectorRepo.findByStation(station);
        
        // Adjust mapping based on YOUR ChargingStation entity fields
        return StationResponse.builder()
                .stationId(station.getStationId())
                // .name(station.getName())
                // .location(station.getLocation())
                // .status(station.getStatus())
                // .latitude(station.getLatitude())
                // .longitude(station.getLongitude())
                .totalConnectors(connectors.size())
                // .lastHeartbeat(station.getLastHeartbeat())
                .build();
    }
}