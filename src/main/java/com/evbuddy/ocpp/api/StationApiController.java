
package com.evbuddy.ocpp.api;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evbuddy.ocpp.dto.ApiResponse;
import com.evbuddy.ocpp.dto.StationResponse;
import com.evbuddy.ocpp.service.StationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stations/all")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StationApiController {
	private final StationService stationService;
    
    @GetMapping
    public ResponseEntity<List<StationResponse>> getAllStations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location) {
        log.info("Get all stations - Status: {}, Location: {}", status, location);
        List<StationResponse> stations = stationService.getAllStations(status, location);
        return ResponseEntity.ok(stations);
    }
    
    @GetMapping("/{stationId}")
    public ResponseEntity<StationResponse> getStationDetails(@PathVariable String stationId) {
        log.info("Get station details for: {}", stationId);
        StationResponse station = stationService.getStationDetails(stationId);
        return ResponseEntity.ok(station);
    }
    
    @GetMapping("/{stationId}/availability")
    public ResponseEntity<ApiResponse> checkAvailability(@PathVariable String stationId) {
        log.info("Check availability for station: {}", stationId);
        boolean available = stationService.checkAvailability(stationId);
        return ResponseEntity.ok(ApiResponse.success(
                available ? "Station is available" : "Station is not available",
                available
        ));
    }
}
