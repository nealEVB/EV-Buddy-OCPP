package com.evbuddy.ocpp.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @Table(name="meter_value")
public class MeterValue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChargingStation station;

    private Integer connectorId;
    private Integer transactionIdOcpp;
    private Instant timestamp;
    private long valueWh;
    
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    private Double energyKWh;

    public Transaction getTransaction() {
		return transaction;
	}
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	// getters/setters
    public void setStation(ChargingStation s){ this.station = s; }
    public void setConnectorId(Integer i){ this.connectorId = i; }
    public void setTransactionIdOcpp(Integer i){ this.transactionIdOcpp = i; }
    public void setTimestamp(Instant t){ this.timestamp = t; }
    public void setValueWh(long v){ this.valueWh = v; }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ChargingStation getStation() {
		return station;
	}
	public Integer getConnectorId() {
		return connectorId;
	}
	public Integer getTransactionIdOcpp() {
		return transactionIdOcpp;
	}
	public Instant getTimestamp() {
		return timestamp;
	}
	public long getValueWh() {
		return valueWh;
	}
	public Double getEnergyKWh() {
		return energyKWh;
	}
	public void setEnergyKWh(Double energyKWh) {
		this.energyKWh = energyKWh;
	}
    
    
}
