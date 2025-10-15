package com.example.ocpp.api;

import com.example.ocpp.domain.ChargingStation;
import com.example.ocpp.domain.Connector;
import com.example.ocpp.dto.ApiResponse;
import com.example.ocpp.dto.ConnectorRequest;
import com.example.ocpp.dto.StatusUpdateRequest;
import com.example.ocpp.repo.ChargingStationRepo;
import com.example.ocpp.repo.ConnectorRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/connectors")
@RequiredArgsConstructor
public class ConnectorController {

    private final ConnectorRepo connectorRepo;
    private final ChargingStationRepo stationRepo;

    //  1. Create a new connector for a station
    @PostMapping
    public ResponseEntity<ApiResponse> createConnector(@RequestBody ConnectorRequest request) {
        log.info(" Creating connector {} for station {}", request.getConnectorId(), request.getStationId());

        ChargingStation station = stationRepo.findByStationId(request.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found: " + request.getStationId()));

        //  Prevent duplicate (station_id + connector_id)
        boolean exists = connectorRepo.findByStation_StationIdAndConnectorId(
                request.getStationId(),
                request.getConnectorId()
        ).isPresent();

        if (exists) {
            String msg = String.format(
                    "Connector ID %d already exists for station %s",
                    request.getConnectorId(),
                    request.getStationId()
            );
            log.warn(" {}", msg);
            return ResponseEntity.badRequest().body(ApiResponse.failure(msg));
        }

        //  Case-insensitive enum conversion
        Connector.Status status = Arrays.stream(Connector.Status.values())
                .filter(s -> s.name().equalsIgnoreCase(request.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid status value: " + request.getStatus()));

        Connector connector = Connector.builder()
                .connectorId(request.getConnectorId())
                .status(status)
                .station(station)
                .build();

        Connector saved = connectorRepo.save(connector);
        log.info("Connector {} created successfully for station {}", saved.getConnectorId(), station.getStationId());

        return ResponseEntity.ok(ApiResponse.success("Connector created successfully", saved));
    }

    //  2. Get all connectors for a specific station
    @GetMapping("/by-station/{stationId}")
    public ResponseEntity<ApiResponse> getConnectorsByStation(@PathVariable String stationId) {
        log.info(" Fetching connectors for station {}", stationId);

        List<Connector> connectors = connectorRepo.findByStationId(stationId);
        return ResponseEntity.ok(ApiResponse.success("Connectors fetched successfully", connectors));
    }

    //  3. Update connector status
    @PutMapping("/status")
    public ResponseEntity<ApiResponse> updateConnectorStatus(
            @RequestParam String stationId,
            @RequestParam Integer connectorId,
            @RequestBody StatusUpdateRequest request) {

        log.info(" Updating status for connector {} at station {}", connectorId, stationId);

        Connector connector = connectorRepo.findByStation_StationIdAndConnectorId(stationId, connectorId)
                .orElseThrow(() -> new RuntimeException("Connector not found for station: " + stationId));

        //  Case-insensitive conversion
        Connector.Status newStatus = Arrays.stream(Connector.Status.values())
                .filter(s -> s.name().equalsIgnoreCase(request.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid status value: " + request.getStatus()));

        connector.setStatus(newStatus);
        connectorRepo.save(connector);

        log.info(" Updated connector {} status to {} for station {}", connectorId, newStatus, stationId);
        return ResponseEntity.ok(ApiResponse.success("Connector status updated successfully", connector));
    }

    //  4. Delete connector by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteConnector(@PathVariable Long id) {
        log.info(" Deleting connector with ID {}", id);

        connectorRepo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Connector deleted successfully"));
    }
}
