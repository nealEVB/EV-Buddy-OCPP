package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.StatusNotificationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("StatusNotification")
public class StatusNotificationService implements OcppService {
    private static final Logger logger = LoggerFactory.getLogger(StatusNotificationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object process(JsonNode payload) {
        try {
            StatusNotificationRequest request = objectMapper.treeToValue(payload, StatusNotificationRequest.class);
            logger.info("Status Notification from Connector {}: Status={}, ErrorCode={}",
                    request.getConnectorId(), request.getStatus(), request.getErrorCode());
            
            // Logic to update the real-time status of the charge point and its connector in your system would go here.
            // This is crucial for displaying correct availability on a user map, for example.
            
        } catch (Exception e) {
            logger.error("Error processing StatusNotification", e);
        }
        
        // The OCPP 1.6 spec does not require a response for StatusNotification, so we return null.
        return null;
    }
}