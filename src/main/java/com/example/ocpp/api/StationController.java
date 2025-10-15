package com.example.ocpp.api;

import com.example.ocpp.domain.ChargingStation;
import com.example.ocpp.dto.ApiResponse;
import com.example.ocpp.repo.ChargingStationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final ChargingStationRepo stationRepo;

    /**
     * ✅ 1. Create or Update Station
     * If stationId exists, update it. Otherwise, create a new record.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createOrUpdateStation(@RequestBody ChargingStation request) {
        try {
            ChargingStation existing = stationRepo.findByStationId(request.getStationId())
                    .orElse(null);

            if (existing != null) {
                // Update existing record
                log.info("Updating existing station: {}", request.getStationId());
                existing.setModel(request.getModel());
                existing.setVendor(request.getVendor());
                existing.setStatus(request.getStatus());
                existing.setFirmwareVersion(request.getFirmwareVersion());
                existing.setVersion(request.getVersion());
                existing.setLastHeartbeat(request.getLastHeartbeat());

                ChargingStation updated = stationRepo.save(existing);
                return ResponseEntity.ok(ApiResponse.success("Station updated successfully", updated));
            } else {
                // Create new record
                log.info("Creating new station: {}", request.getStationId());
                ChargingStation saved = stationRepo.save(request);
                return ResponseEntity.ok(ApiResponse.success("Station created successfully", saved));
            }

        } catch (Exception e) {
            log.error("Error saving station: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("Failed to create or update station: " + e.getMessage()));
        }
    }

    /**
     * ✅ 2. Get all stations
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllStations() {
        List<ChargingStation> stations = stationRepo.findAll();
        return ResponseEntity.ok(ApiResponse.success("All stations fetched successfully", stations));
    }

    /**
     * ✅ 3. Get station by ID
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<ApiResponse> getStationById(@PathVariable String stationId) {
        return stationRepo.findByStationId(stationId)
                .map(station -> ResponseEntity.ok(ApiResponse.success("Station found", station)))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(ApiResponse.failure("Station not found: " + stationId)));
    }

    /**
     * ✅ 4. Delete station by ID
     */
    @DeleteMapping("/{stationId}")
    public ResponseEntity<ApiResponse> deleteStation(@PathVariable String stationId) {
        return stationRepo.findByStationId(stationId)
                .map(station -> {
                    stationRepo.delete(station);
                    return ResponseEntity.ok(ApiResponse.success("Station deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(ApiResponse.failure("Station not found: " + stationId)));
    }
}
