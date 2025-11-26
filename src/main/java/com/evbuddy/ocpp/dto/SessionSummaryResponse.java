package com.evbuddy.ocpp.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionSummaryResponse {
    private Long transactionId;
    private String stationId;
    private Integer connectorId;
    private String tokenId;

    private Instant startTime;
    private Instant endTime;
    private long durationSeconds;

    private long startWh;
    private long endWh;
    private long energyWh;

    /** Optional cost if you enable pricing in the service */
    private Double cost;
}
