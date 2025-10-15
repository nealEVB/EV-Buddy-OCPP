package com.example.ocpp.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final SimpMessagingTemplate simp;

    public void publishToDashboard(String topic, Object payload) {
        simp.convertAndSend("/topic/" + topic, payload);
    }
    public void sendMeter(Long txId, Object payload) {
        simp.convertAndSend("/topic/transactions/" + txId + "/meter", payload);
    }

    public void sendTxStatus(Long txId, String status) {
        simp.convertAndSend("/topic/transactions/" + txId + "/status", Map.of("transactionId", txId, "status", status));
    }

    public void sendStation(String stationId, Object payload) {
        simp.convertAndSend("/topic/stations/" + stationId, payload);
    }
}
