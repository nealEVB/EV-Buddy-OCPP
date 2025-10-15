package handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import service.OcppService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
public class CentralSystemHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(CentralSystemHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, OcppService> ocppServiceMap;

    public CentralSystemHandler(Map<String, OcppService> ocppServiceMap) {
        this.ocppServiceMap = ocppServiceMap;
        logger.info("✅ CentralSystemHandler initialized with services: {}", ocppServiceMap.keySet());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String chargePointId = getChargePointId(session);
        logger.info("✅ Charge point connected: {} (Session ID: {})", chargePointId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String chargePointId = getChargePointId(session);

        JsonNode jsonNode = objectMapper.readTree(payload);
        int messageTypeId = jsonNode.get(0).asInt();
        String messageId = jsonNode.get(1).asText();
        String action = jsonNode.get(2).asText();
        JsonNode payloadNode = jsonNode.get(3);

        logger.info("➡️ Received from [{}]: Action='{}', Payload={}", chargePointId, action, payloadNode.toString());

        if (messageTypeId == 2) { // CALL from Charge Point
            OcppService service = ocppServiceMap.get(action);
            if (service != null) {
                Object confirmationPayload = service.process(payloadNode);
                if (confirmationPayload != null) {
                    Object[] response = new Object[]{3, messageId, confirmationPayload}; // CALLRESULT
                    String responseJson = objectMapper.writeValueAsString(response);
                    logger.info("⬅️ Sending to [{}]: {}", chargePointId, responseJson);
                    session.sendMessage(new TextMessage(responseJson));
                }
            } else {
                logger.warn("No service found for action: '{}'", action);
            }
        }
    }

    private String getChargePointId(WebSocketSession session) {
        if (session.getUri() == null) return "unknown";
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
