package service;

import com.fasterxml.jackson.databind.JsonNode;

import dto.HeartbeatConfirmation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service("Heartbeat")
public class HeartbeatService implements OcppService {
    
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    @Override
    public Object process(JsonNode payload) {
        logger.info("Received Heartbeat.");
        
        // Respond with the current time to acknowledge the heartbeat.
        HeartbeatConfirmation confirmation = new HeartbeatConfirmation();
        confirmation.setCurrentTime(Instant.now().toString());
        return confirmation;
    }
}
