package com.example.ocpp.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ocpp.domain.ChargingStation;
import com.example.ocpp.domain.Connector;

public interface ConnectorRepo extends JpaRepository<Connector, Long> {

    // ✅ find connector by stationId and connectorId
    Optional<Connector> findByStation_StationIdAndConnectorId(String stationId, Integer connectorId);

    // ✅ find all connectors for a station (entity reference)
    List<Connector> findByStation(ChargingStation station);

    // ✅ find by station and connectorId using entity + id
    @Query("SELECT c FROM Connector c WHERE c.station = :station AND c.connectorId = :connectorId")
    Optional<Connector> findByStationAndConnectorId(
        @Param("station") ChargingStation station,
        @Param("connectorId") Integer connectorId
    );

    // ✅ FIXED: use station.stationId (String), not station.id (Long)
    @Query("SELECT c FROM Connector c WHERE c.station.stationId = :stationId")
    List<Connector> findByStationId(@Param("stationId") String stationId);
}
