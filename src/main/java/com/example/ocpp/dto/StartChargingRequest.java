package com.example.ocpp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartChargingRequest {
    
    @NotBlank(message = "Charging station ID is required")
    private String chargingStationId;
    
    @NotNull(message = "Connector number is required")
    private Integer connectorNumber;
}