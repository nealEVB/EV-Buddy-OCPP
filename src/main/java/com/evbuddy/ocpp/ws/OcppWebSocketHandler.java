package com.evbuddy.ocpp.ws;

import com.evbuddy.ocpp.service.OcppService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class OcppWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(OcppWebSocketHandler.class);

    private final SessionRegistry sessions;
    private final OcppMessageCodec codec;
    private final OcppService ocppService;

    public OcppWebSocketHandler(SessionRegistry sessions, OcppMessageCodec codec, OcppService ocppService){
        this.sessions = sessions;
        this.codec = codec;
        this.ocppService = ocppService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = session.getUri().getPath();
        String stationId = path.substring(path.lastIndexOf('/') + 1);
        sessions.put(stationId, session);
        log.info("Station {} connected: {}", stationId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode frame = codec.parse(message.getPayload());
        int type = frame.get(0).asInt();
        String uniqueId = frame.get(1).asText();
        if (type == 2) {
            String action = frame.get(2).asText();
            JsonNode payload = frame.get(3);
            String response = ocppService.handleCall(session, uniqueId, action, payload);
            session.sendMessage(new TextMessage(response));
        } else if (type == 3) {
            ocppService.handleCallResult(uniqueId, frame.get(2));
        } else if (type == 4) {
            log.warn("CALLERROR {}: {}", uniqueId, frame.toString());
            ocppService.handleCallError(uniqueId, frame);
        } else {
            log.error("Unknown frame type: {}", type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Session {} closed: {}", session.getId(), status);
    }
}
