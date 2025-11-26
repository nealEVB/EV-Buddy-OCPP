package com.evbuddy.ocpp.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SessionRegistry {
    private final Map<String, WebSocketSession> sessionsByStation = new ConcurrentHashMap<>();

    public void put(String stationId, WebSocketSession session){ sessionsByStation.put(stationId, session); }
    public WebSocketSession get(String stationId){ return sessionsByStation.get(stationId); }
    public void remove(String stationId){ sessionsByStation.remove(stationId); }
    public boolean isConnected(String stationId){ return sessionsByStation.containsKey(stationId); }
}
