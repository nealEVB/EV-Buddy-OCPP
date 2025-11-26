package com.evbuddy.ocpp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(
    name = "connector",
    uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "connector_id"})
)
public class Connector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonBackReference
    private ChargingStation station;

    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        AVAILABLE,
        OCCUPIED,
        FAULTED,
        UNAVAILABLE;

        // ✅ Case-insensitive JSON to Enum conversion
        @JsonCreator
        public static Status fromValue(String value) {
            for (Status s : Status.values()) {
                if (s.name().equalsIgnoreCase(value)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Invalid connector status: " + value);
        }

        // ✅ Prettify Enum → JSON (e.g. "Available" instead of "AVAILABLE")
        @JsonValue
        public String toValue() {
            String lowercase = name().toLowerCase();
            return Character.toUpperCase(lowercase.charAt(0)) + lowercase.substring(1);
        }
    }

    // ✅ Automatically normalize before saving to DB
    @PrePersist
    @PreUpdate
    public void normalizeStatus() {
        if (status != null) {
            status = Status.valueOf(status.name().toUpperCase());
        }
    }
}
