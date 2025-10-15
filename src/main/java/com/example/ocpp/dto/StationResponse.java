package com.example.ocpp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationResponse {
    private String stationId;
    private String name;
    private String location;
    private String status;
    private Double latitude;
    private Double longitude;
    private Integer totalConnectors;
    private LocalDateTime lastHeartbeat;
}
