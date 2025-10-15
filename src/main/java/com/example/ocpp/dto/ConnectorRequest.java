package com.example.ocpp.dto;

import lombok.Data;

@Data
public class ConnectorRequest {
    private String stationId;
    private Integer connectorId;
    private String status;
}