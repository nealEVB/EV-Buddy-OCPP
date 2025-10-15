package com.example.ocpp.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChargingStation station;
    private Instant endTime;
    private String tokenId; 

    public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}
	private Integer connectorId;

    // OCPP protocol transaction id
    private Integer transactionIdOcpp;

    @Column(nullable = false)
    private String idTag;

    private Instant startTime;
    private Instant stopTime;
    private Double startMeterWh;
    private Double stopMeterWh;

    @Enumerated(EnumType.STRING)
    private State state;

    @Version
    private Long version;

    public enum State { STARTED, STOPPED, FAILED }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // --- Factory ---
    public static Transaction of(
            ChargingStation station,
            Integer connectorId,
            Integer transactionIdOcpp,
            String idTag,
            Instant start,
            Double startMeterWh,
            State state) {
        Transaction t = new Transaction();
        t.station = station;
        t.connectorId = connectorId;
        t.transactionIdOcpp = transactionIdOcpp;
        t.idTag = idTag;
        t.startTime = start;
        t.startMeterWh = startMeterWh;
        t.state = state;
        return t;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChargingStation getStation() { return station; }
    public void setStation(ChargingStation station) { this.station = station; }

    public Integer getConnectorId() { return connectorId; }
    public void setConnectorId(Integer connectorId) { this.connectorId = connectorId; }

    public Integer getTransactionIdOcpp() { return transactionIdOcpp; }
    public void setTransactionIdOcpp(Integer transactionIdOcpp) { this.transactionIdOcpp = transactionIdOcpp; }

    public String getIdTag() { return idTag; }
    public void setIdTag(String idTag) { this.idTag = idTag; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getStopTime() { return stopTime; }
    public void setStopTime(Instant stopTime) { this.stopTime = stopTime; }

    public Double getStartMeterWh() { return startMeterWh; }
    public void setStartMeterWh(Double startMeterWh) { this.startMeterWh = startMeterWh; }

    public Double getStopMeterWh() { return stopMeterWh; }
    public void setStopMeterWh(Double stopMeterWh) { this.stopMeterWh = stopMeterWh; }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
