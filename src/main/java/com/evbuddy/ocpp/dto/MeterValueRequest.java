package com.evbuddy.ocpp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterValueRequest {
    /** Existing transaction id returned by /api/transactions/start */
    @NotNull
    private Long transactionId;

    /** Meter reading in Watt-hours (Wh). If you measure in kWh, multiply by 1000. */
    @NotNull @Min(0)
    private Long valueWh;

    /** Unix epoch milliseconds. If null, server will use current time. */
    private Long timestamp;
}