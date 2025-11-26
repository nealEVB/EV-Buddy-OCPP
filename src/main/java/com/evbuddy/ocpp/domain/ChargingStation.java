package com.evbuddy.ocpp.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity @Table(name="charging_station")
public class ChargingStation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_id", nullable = false)
    private String stationId;

    private String vendor;
    private String model;
    private String firmwareVersion;

    @Enumerated(EnumType.STRING)
    private StationStatus status;

    private Instant lastHeartbeat;

    @Version
    private Long version;

   // @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Connector> connectors = new ArrayList<>();

    public enum StationStatus { Available, Preparing, Charging, SuspendedEV, SuspendedEVSE, Finishing, Faulted, Unavailable }

    // getters/setters
    public Long getId(){ return id; }
    public String getStationId(){ return stationId; }
    public void setStationId(String s){ this.stationId = s; }
    public String getVendor(){ return vendor; }
    public void setVendor(String v){ this.vendor = v; }
    public String getModel(){ return model; }
    public void setModel(String m){ this.model = m; }
    public String getFirmwareVersion(){ return firmwareVersion; }
    public void setFirmwareVersion(String f){ this.firmwareVersion = f; }
    public StationStatus getStatus(){ return status; }
    public void setStatus(StationStatus s){ this.status = s; }
    public Instant getLastHeartbeat(){ return lastHeartbeat; }
    public void setLastHeartbeat(Instant t){ this.lastHeartbeat = t; }
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public List<Connector> getConnectors() {
		return connectors;
	}
	public void setConnectors(List<Connector> connectors) {
		this.connectors = connectors;
	}
	public void setId(Long id) {
		this.id = id;
	}
    
    
    
    
}
