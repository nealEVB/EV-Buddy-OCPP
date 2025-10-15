package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.BootNotificationConfirmation;
import dto.BootNotificationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service("BootNotification")
public class BootNotificationService implements OcppService {
    private static final Logger logger = LoggerFactory.getLogger(BootNotificationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object process(JsonNode payload) {
        try {
            BootNotificationRequest request = objectMapper.treeToValue(payload, BootNotificationRequest.class);
            logger.info("Processing BootNotification for vendor: {}, model: {}", 
                request.getChargePointVendor(), request.getChargePointModel());

            // In a real system, you would save the charge point details to a database here.

            BootNotificationConfirmation confirmation = new BootNotificationConfirmation();
            confirmation.setCurrentTime(Instant.now().toString());
            confirmation.setInterval(300); // Set heartbeat interval to 5 minutes
            confirmation.setStatus("Accepted");
            
            return confirmation;
        } catch (Exception e) {
            logger.error("Error processing BootNotification", e);
            // If processing fails, reject the connection.
            BootNotificationConfirmation confirmation = new BootNotificationConfirmation();
            confirmation.setCurrentTime(Instant.now().toString());
            confirmation.setInterval(300);
            confirmation.setStatus("Rejected");
            return confirmation;
        }
    }
}
