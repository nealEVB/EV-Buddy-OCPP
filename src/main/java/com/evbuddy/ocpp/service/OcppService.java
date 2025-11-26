package com.evbuddy.ocpp.service;

import com.evbuddy.ocpp.domain.*;
import com.evbuddy.ocpp.repo.*;
import com.evbuddy.ocpp.server.OcppAction;
import com.evbuddy.ocpp.server.payload.*;
import com.evbuddy.ocpp.ws.OcppMessageCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

@Service
public class OcppService {
    private static final Logger log = LoggerFactory.getLogger(OcppService.class);

    private final ChargingStationRepo stationRepo;
    private final ConnectorRepo connectorRepo;
    private final TransactionRepo txRepo;
    private final MeterValueRepo mvRepo;
    private final UserTokenRepo tokenRepo;
    private final OcppMessageCodec codec;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    public OcppService(ChargingStationRepo stationRepo, ConnectorRepo connectorRepo, TransactionRepo txRepo,
                       MeterValueRepo mvRepo, UserTokenRepo tokenRepo, OcppMessageCodec codec){
        this.stationRepo = stationRepo;
        this.connectorRepo = connectorRepo;
        this.txRepo = txRepo;
        this.mvRepo = mvRepo;
        this.tokenRepo = tokenRepo;
        this.codec = codec;
    }

    public String handleCall(WebSocketSession session, String uid, String action, JsonNode payload) throws Exception {
        OcppAction a = OcppAction.valueOf(action);
        switch (a) {
            case BootNotification:
                return callResult(uid, mapper.valueToTree(handleBoot(session, payload)));
            case Heartbeat:
                return callResult(uid, mapper.valueToTree(handleHeartbeat(session)));
            case Authorize:
                return callResult(uid, mapper.valueToTree(handleAuthorize(payload)));
            case StartTransaction:
                return callResult(uid, mapper.valueToTree(handleStartTx(session, payload)));
            case StopTransaction:
                return callResult(uid, mapper.valueToTree(handleStopTx(session, payload)));
            case MeterValues:
                return callResult(uid, mapper.valueToTree(handleMeterValues(session, payload)));
            case StatusNotification:
                return callResult(uid, mapper.valueToTree(handleStatusNotification(session, payload)));
            default:
                throw new IllegalArgumentException("Unknown action: " + a);
        }
    }

    public void handleCallResult(String uid, JsonNode payload){
        log.info("CALLRESULT {} -> {}", uid, payload.toString());
    }

    public void handleCallError(String uid, JsonNode frame){
        log.warn("CALLERROR {} -> {}", uid, frame.toString());
    }

    private String callResult(String uid, JsonNode payload) throws Exception {
        com.fasterxml.jackson.databind.node.ArrayNode root = mapper.createArrayNode();
        root.add(3); root.add(uid); root.add(payload);
        return mapper.writeValueAsString(root);
    }

    @Transactional
    protected BootNotificationPayloads.Res handleBoot(WebSocketSession session, JsonNode p){
        String stationId = session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf('/')+1);
        BootNotificationPayloads.Req req = mapper.convertValue(p, BootNotificationPayloads.Req.class);
        ChargingStation station = stationRepo.findByStationId(stationId).orElseGet(ChargingStation::new);
        station.setStationId(stationId);
        station.setVendor(req.chargePointVendor);
        station.setModel(req.chargePointModel);
        station.setFirmwareVersion(req.firmwareVersion);
        station.setLastHeartbeat(Instant.now());
        station.setStatus(ChargingStation.StationStatus.Available);
        stationRepo.save(station);
        return new BootNotificationPayloads.Res("Accepted", Instant.now().toString(), 60);
    }

    @Transactional
    protected Map<String,String> handleHeartbeat(WebSocketSession session){
        String stationId = session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf('/')+1);
        stationRepo.findByStationId(stationId).ifPresent(s -> { s.setLastHeartbeat(Instant.now()); stationRepo.save(s); });
        Map<String,String> res = new HashMap<>();
        res.put("currentTime", Instant.now().toString());
        return res;
    }

    @Transactional(readOnly = true)
    protected AuthorizePayloads.Res handleAuthorize(JsonNode p){
        AuthorizePayloads.Req req = mapper.convertValue(p, AuthorizePayloads.Req.class);
        Optional<UserToken> tok = tokenRepo.findByIdTag(req.idTag);
        String status = tok.isPresent() && tok.get().isActive() ? "Accepted" : "Invalid";
        return new AuthorizePayloads.Res(new AuthorizePayloads.IdTagInfo(status));
    }

    @Transactional
    protected Map<String,Object> handleStatusNotification(WebSocketSession session, JsonNode p){
        String stationId = session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf('/')+1);
        int connectorId = p.get("connectorId").asInt();
        String status = p.get("status").asText();
        ChargingStation station = stationRepo.findByStationId(stationId).orElseThrow();
        java.util.Optional<Connector> opt = connectorRepo.findByStation_StationIdAndConnectorId(stationId, connectorId);
        Connector conn = opt.orElseGet(() -> {
            Connector c = new Connector();
            c.setStation(station);
            c.setConnectorId(connectorId);
            return c;
        });
        conn.setStatus(Connector.Status.valueOf(status));
        connectorRepo.save(conn);
        station.setStatus(mapStationStatus(status));
        stationRepo.save(station);
        return new java.util.HashMap<>();
    }

    private ChargingStation.StationStatus mapStationStatus(String status){
        if ("Available".equals(status)) return ChargingStation.StationStatus.Available;
        if ("Charging".equals(status)) return ChargingStation.StationStatus.Charging;
        if ("Faulted".equals(status)) return ChargingStation.StationStatus.Faulted;
        return ChargingStation.StationStatus.Unavailable;
    }

    @Transactional
    protected Map<String,Object> handleStartTx(WebSocketSession session, JsonNode p){
        String stationId = session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf('/')+1);
        int connectorId = p.get("connectorId").asInt();
        String idTag = p.get("idTag").asText();
        int txId = p.path("transactionId").asInt(0);
        double meterStart = p.path("meterStart").asDouble(0);

        ChargingStation station = stationRepo.findByStationId(stationId).orElseThrow();
        if (txId == 0) {
            txId = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
        }
        Transaction tx = Transaction.of(station, connectorId, txId, idTag, Instant.now(), meterStart, Transaction.State.STARTED);
        txRepo.save(tx);
        Map<String,Object> res = new HashMap<>();
        res.put("transactionId", tx.getTransactionIdOcpp());
        java.util.Map<String,String> idTagInfo = new java.util.HashMap<>();
        idTagInfo.put("status", "Accepted");
        res.put("idTagInfo", idTagInfo);
        return res;
    }

    @Transactional
    protected Map<String,Object> handleStopTx(WebSocketSession session, JsonNode p){
        String stationId = session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf('/')+1);
        int txId = p.get("transactionId").asInt();
        double meterStop = p.path("meterStop").asDouble(0);
        Transaction tx = txRepo.findByStation_StationIdAndTransactionIdOcpp(stationId, txId).orElseThrow();
        tx.setStopTime(Instant.now());
        tx.setStopMeterWh(meterStop);
        tx.setState(Transaction.State.STOPPED);
        txRepo.save(tx);
        Map<String,Object> res = new HashMap<>();
        java.util.Map<String,String> idTagInfo = new java.util.HashMap<>();
        idTagInfo.put("status", "Accepted");
        res.put("idTagInfo", idTagInfo);
        return res;
    }

    @Transactional
    protected Map<String,Object> handleMeterValues(WebSocketSession session, JsonNode p){
        String stationId = session.getUri().getPath().substring(session.getUri().getPath().lastIndexOf('/')+1);
        int connectorId = p.get("connectorId").asInt();
        int txId = p.path("transactionId").asInt(0);
        ChargingStation station = stationRepo.findByStationId(stationId).orElseThrow();
        MeterValue mv = new MeterValue();
        mv.setStation(station);
        mv.setConnectorId(connectorId);
        mv.setTransactionIdOcpp(txId);
        mv.setTimestamp(Instant.now());
        mv.setValueWh(p.path("valueWh").asLong(0));
        mvRepo.save(mv);
        return new java.util.HashMap<>();
    }
    
   // ADD THESE NEW METHODS:
    
    public Integer startRemoteTransaction(String stationId, Integer connectorNumber, String idTag) {
        log.info("Starting remote transaction for station: {}, connector: {}", stationId, connectorNumber);
        
        // TODO: Implement using your existing OCPP WebSocket handler
        // Send OCPP RemoteStartTransaction or StartTransaction message
        // Wait for response and return transactionId
        
        // Placeholder - return random transaction ID for now
        return (int) (Math.random() * 10000);
    }
    
    public void stopRemoteTransaction(String stationId, Integer transactionId) {
        log.info("Stopping remote transaction: {} for station: {}", transactionId, stationId);
        
        // TODO: Implement using your existing OCPP WebSocket handler
        // Send OCPP RemoteStopTransaction or StopTransaction message
    }
}
