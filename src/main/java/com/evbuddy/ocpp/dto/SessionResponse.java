package com.evbuddy.ocpp.dto;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    private Long transactionId;
    private String chargingStationId;
    private String stationName;
    private Integer connectorNumber;
    private Instant startTime;
    private Instant endTime;
    private Long durationMinutes;
    private Double energyConsumedKwh;
    private Double cost;
    private String status;
}	